package com.wmp.whetstone.extraPanel.classForm.panel;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.easteregg.EasterEgg;
import com.wmp.PublicTools.easteregg.EasterEggClear;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.recording.main.Recording;
import com.wmp.recording.tools.GetRecordingInfo;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.extraPanel.classForm.CFInfoControl;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfo;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfos;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ClassFormPanel extends CTViewPanel<ClassFormInfos[]> {

    private String oldNowClassName = "无";
    private String oldNextClassName = "无";

    private static AtomicReference<Recording.Info> recordingInfo = new AtomicReference<>();


    public ClassFormPanel() {
        this.setLayout(new GridBagLayout());
        this.setName("课程表");
        this.setID("ClassFormPanel");
        this.setOpaque(false);

        this.setIgnoreState(true);
        this.setIndependentRefresh(true, 1000);
    }

    @Override
    public CTInfoControl<ClassFormInfos[]> setInfoControl() {
        return new CFInfoControl();
    }

    @Override
    protected void easyRefresh() {
        synchronized (this) {
            this.removeAll();


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

                    if (nowClass.className().equals("无")){
                        //清理彩蛋
                        EasterEggClear.INSTANCE.UHelper(0);



                    }else{

                        new Thread(()->{
                            try {
                                //U盘助手
                                if (containsTheClass(finalNowClass, "数学", "化学", "班会", "劳动", "晨会")){
                                    EasterEgg.INSTANCE.UHelper(2);
                                }

                                //重启Explorer
                                {
                                    if (containsTheClass(finalNowClass, "晨会", "化学")) {
                                        EasterEgg.INSTANCE.reStartExplorer();
                                    }
                                }

                                //锤子
                                /*if (containsTheClass(finalNowClass, "体育")) {
                                    happenError();
                                }*/

                                //全屏遮挡
                                if (containsTheClass(finalNowClass, "数学", "班会", "劳动", "晨会")) {
                                    for (int i = 0; i < 5; i++) {
                                        EasterEgg.INSTANCE.screenBlocking(5, 1);
                                    }
                                }



                                //播放
                                if (containsTheClass(finalNowClass, "语文", "体育")) {
                                    EasterEgg.INSTANCE.videoPlayer();
                                }
                                //NJ接管
                                /*if (containsTheClass(finalNowClass, "语文", "英语", "物理", "生物", "体育")) {
                                    banZhuRenChuMo();
                                }*/

                                //窗口透明
                                /*if (containsTheClass(finalNowClass, "英语", "物理", "化学", "体育")) {
                                    setAllFrameGlass();
                                }*/
                            } catch (Exception _) {
                                Log.trayIcon.displayMessage("噢,天呐!", "搞砸了呢...", TrayIcon.MessageType.ERROR);
                            }
                        }, "彩蛋启动!").start();


                    }

                    if (recordingInfo.get() != null){
                        Recording.stop(recordingInfo.get());
                    }

                    //生成路径+名字
                    StringBuffer sb = new StringBuffer("Whetstone\\recording\\");

                    SimpleDateFormat date = new SimpleDateFormat("yyyy_MM_dd");
                    sb.append(date.format(new Date())).append("\\");

                    SimpleDateFormat time = new SimpleDateFormat("HH_mm");
                    sb.append(time.format(new Date())).append("_");

                    sb.append(finalNowClass.className()).
                            append(".wav");

                    new Thread(()->{
                        {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject = new JSONObject(
                                        IOForInfo.getInfos(new File(CTInfo.DATA_PATH, "Whetstone\\recording.json").toURI().toURL()));
                            } catch (Exception _) {
                            }

                            if (jsonObject.has("mixerInfo")) {
                                JSONObject finalJsonObject = jsonObject;
                                GetRecordingInfo.enumerateInputDevices().forEach(info ->{
                                    if (info.getName().equals(finalJsonObject.getString("mixerInfo"))){
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


                // 数据更新
                this.oldNowClassName = nowClass.className();
                this.oldNextClassName = nextClass;


            } catch (Exception e) {
                Log.err.print(getClass(), "获取课程表失败", e);
            }



        }
    }



    /**
     * 判断<code>classFormInfo</code>对应的课程是否在<code>classes</code>列表中
     * @param classFormInfo 课程
     * @param classes 课程列表
     * @return 若<code>classes</code>列表中的一项包含<code>classFormInfo</code>对应的课程,则返回<code>true</code>
     */
    private static boolean containsTheClass(ClassFormInfo classFormInfo, String... classes){
        for (String aClass : classes) {
            if (classFormInfo.className().contains(aClass)) return true;
        }
        return false;
    }
}
