package com.wmp.whetstone.extraPanel.classForm.panel;


import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.easter_egg_control.EasterEggRun;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.recording.main.Recording;
import com.wmp.recording.tools.GetRecordingInfo;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.extraPanel.classForm.CFInfoControl;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfo;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfos;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClassFormPanel extends CTViewPanel<ClassFormInfos[]> {

    private static final AtomicReference<Recording.Info> recordingInfo = new AtomicReference<>();
    private String oldNowClassName = "无";
    private String oldNextClassName = "无";


    public ClassFormPanel() {
        this.setName("课程表");
        this.setID("ClassFormPanel");

        this.setIgnoreState(true);
        this.setIndependentRefresh(true, 1000);
    }

    /**
     * 递归删除目录及其所有内容
     *
     * @param directory 要删除的目录
     */
    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

    /**
     * 判断<code>classFormInfo</code>对应的课程是否在<code>classes</code>列表中
     *
     * @param classFormInfo 课程
     * @param classes       课程列表
     * @return 若<code>classes</code>列表中的一项包含<code>classFormInfo</code>对应的课程,则返回<code>true</code>
     */
    private static boolean containsTheClass(ClassFormInfo classFormInfo, String... classes) {
        for (String aClass : classes) {
            if (classFormInfo.className().contains(aClass)) return true;
        }
        return false;
    }

    @Override
    public CTInfoControl<ClassFormInfos[]> setInfoControl() {
        return new CFInfoControl();
    }

    @Override
    protected void easyRefresh() {
        synchronized (this) {

            //课程数据
            ClassFormInfo nowClass = ((CFInfoControl) getInfoControl()).getNowClass();
            CFInfoControl.nextClassInfo nextClassInfo = ((CFInfoControl) getInfoControl()).getNextClass();
            String nextClass = nextClassInfo.className();
            try {

                if (nowClass == null) {
                    nowClass = new ClassFormInfo("无", "00:00-00:00");
                }
                if (nextClass == null || nextClass.isEmpty()) nextClass = "无";

                // 使用 Objects.equals 来安全比较，避免 NullPointerException
                if (!Objects.equals(oldNowClassName, nowClass.className()) ||
                        !Objects.equals(oldNextClassName, nextClass)) {

                    ClassFormInfo finalNowClass = nowClass;

                    if (nowClass.className().equals("无")) {
                        EasterEggRun.clear(oldNowClassName);
                    } else {
                        EasterEggRun.run("class_start", "课程开始执行");

                        EasterEggRun.run(finalNowClass.className(), finalNowClass.className() + "彩蛋");

                    }

                    //录音
                    {
                        if (recordingInfo.get() != null) {
                            Recording.stop(recordingInfo.get());
                        }

                        //生成路径+名字
                        StringBuffer sb = new StringBuffer("Whetstone\\Recording\\");

                        SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
                        sb.append(date.format(new Date())).append("\\");

                        SimpleDateFormat time = new SimpleDateFormat("[HH_mm]");
                        sb.append(time.format(new Date()));

                        sb.append("[").append(finalNowClass.className()).append("-").append(nextClassInfo.className()).append("]")
                                .append(".wav");
                        //删除多余的文件夹（若文件夹超过5个）
                        File recording = new File(CTInfo.TEMP_PATH, "Whetstone\\Recording\\");
                        if (recording.exists() && recording.isDirectory()) {
                            File[] files = recording.listFiles(file -> {
                                String directory_format = "^\\d{4}_\\d{2}_\\d{2}$";
                                String name = file.getName();
                                boolean matches = name.matches(directory_format);
                                Log.info.print(ClassFormPanel.class.toString(), "匹配文件夹：" + name + "->" + matches);
                                return matches;
                            });
                            if (files != null) {
                                int recordingDirNum = Integer.parseInt(CTInfo.basicInf.getProperty("recordingDirNum", "5"));
                                if (files.length > recordingDirNum) {
                                    Log.info.print(ClassFormPanel.class.toString(), "符合的文件夹列表：" + Arrays.toString(files));
                                    HashMap<Date, File> map = new HashMap<>();
                                    for (File file : files) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
                                        map.put(dateFormat.parse(file.getName()), file);
                                    }

                                    // 将map的keySet转换为List并排序
                                    List<Date> sortedDates = new ArrayList<>(map.keySet());
                                    sortedDates.sort(Collections.reverseOrder()); // 降序排序，最新的在前

                                    // 获取最大的5个日期（最近的n个）
                                    List<Date> top5Dates = sortedDates.subList(0, recordingDirNum);

                                    // 反选出其余的日期
                                    List<Date> remainingDates = sortedDates.subList(recordingDirNum, sortedDates.size());

                                    // 如果需要获取对应的文件
                                    List<File> top5Files = top5Dates.stream()
                                            .map(map::get)
                                            .toList();

                                    List<File> remainingFiles = remainingDates.stream()
                                            .map(map::get)
                                            .toList();

                                    Log.info.print(ClassFormPanel.class.toString(), "最近的" + recordingDirNum + "个文件夹：" + top5Files);
                                    Log.info.print(ClassFormPanel.class.toString(), "其余的文件夹：" + remainingFiles);

                                    for (File file : remainingFiles) {
                                        if (file.exists() && file.isDirectory()) {
                                            deleteDirectory(file);
                                            Log.info.print(ClassFormPanel.class.toString(), "已删除旧文件夹: " + file.getAbsolutePath());
                                        }
                                    }

                                }
                            }
                        }


                        new Thread(() -> {
                            {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject = new JSONObject(
                                            IOForInfo.getInfos(new File(CTInfo.DATA_PATH, "Whetstone\\recording.json").toURI().toURL()));
                                } catch (Exception _) {
                                }

                                if (jsonObject.has("mixerInfo")) {
                                    JSONObject finalJsonObject = jsonObject;
                                    GetRecordingInfo.enumerateInputDevices().forEach(info -> {
                                        if (info.getName().equals(finalJsonObject.getString("mixerInfo"))) {
                                            Recording.Info tempInfo = Recording.create(info);
                                            recordingInfo.set(tempInfo);
                                        }
                                    });
                                }
                            }

                            Recording.recording(
                                    new File(CTInfo.TEMP_PATH, sb.toString()),
                                    recordingInfo.get());

                        }, "录音线程" + sb).start();
                    }


                }


                // 数据更新
                this.oldNowClassName = nowClass.className();
                this.oldNextClassName = nextClass;


            } catch (Exception e) {
                Log.err.print(getClass(), "获取课程表失败", e);
            }


        }
    }
}
