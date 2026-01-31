package com.wmp.whetstone.extraPanel.classForm;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.DateTools;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 课程表信息控制类
 * ClassFormInfos[] 课程信息(周一到周五)
 */
public class CFInfoControl extends CTInfoControl<ClassFormInfos[]> {

    /**
     * 获取下一节课
     *
     * @return 课程信息
     */
    public ClassFormInfo getNowClass() {
        Calendar calendar = Calendar.getInstance();
        int week = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY -> week = 1;
            case Calendar.TUESDAY -> week = 2;
            case Calendar.WEDNESDAY -> week = 3;
            case Calendar.THURSDAY -> week = 4;
            case Calendar.FRIDAY -> week = 5;
            case Calendar.SATURDAY -> week = 6;
            case Calendar.SUNDAY -> week = 7;
        }
        ClassFormInfos classFormInfos = getInfo()[week - 1];
        for (ClassFormInfo classFormInfo : classFormInfos.classFormInfos()) {
            String[] times = classFormInfo.targetTime().split("-");
            if (DateTools.isInTimePeriod(times[0], times[1])) {
                return classFormInfo;
            }
        }

        return new ClassFormInfo("无", "00:00-00:00");
    }

    @Override
    public File getInfoBasicFile() {
        return new File(CTInfo.DATA_PATH, "ClassForm");
    }

    @Override
    public void setInfo(ClassFormInfos[] classFormInfos) {
        try {
            for (int i = 0; i < 7; i++) {
                if (classFormInfos[i] == null) {
                    continue;
                }

                JSONArray jsonArray = new JSONArray();
                ArrayList<ClassFormInfo> infos = classFormInfos[i].classFormInfos();
                for (ClassFormInfo info : infos) {
                    jsonArray.put(new JSONObject()
                            .put("class", info.className())
                            .put("time", info.targetTime()));
                }

                new IOForInfo(new File(getInfoBasicFile(), (i + 1) + ".json"))
                        .setInfo(jsonArray.toString(4));
            }
        } catch (IOException e) {
            Log.err.print(CFInfoControl.class, "ClassFormInfos.json文件写入失败", e);
        }
    }

    public nextClassInfo getNextClass() {
        Calendar calendar = Calendar.getInstance();
        int week = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY -> week = 1;
            case Calendar.TUESDAY -> week = 2;
            case Calendar.WEDNESDAY -> week = 3;
            case Calendar.THURSDAY -> week = 4;
            case Calendar.FRIDAY -> week = 5;
            case Calendar.SATURDAY -> week = 6;
            case Calendar.SUNDAY -> week = 7;
        }
        if (getInfo() == null) return null;
        ClassFormInfos classFormInfos = getInfo()[week - 1];
        if (classFormInfos == null) return null;
        String[] tempData = new String[3];// 0-时间 1-课程 2-间隔时间

        for (ClassFormInfo classFormInfo : classFormInfos.classFormInfos()) {

            String beginTime = classFormInfo.targetTime().split("-")[0];//获取开始时间

            if (DateTools.getRemainderTime(beginTime) <= 0) {
                continue;
            }

            if (tempData[0] == null ||
                    DateTools.getRemainderTime(beginTime) < DateTools.getRemainderTime(tempData[0])) {
                tempData[0] = beginTime;
                tempData[1] = classFormInfo.className();
                tempData[2] = String.valueOf(DateTools.getRemainderTime(beginTime) / 1000 / 60 + 1);
            }
        }
        return new nextClassInfo(tempData[2], tempData[1]);
    }

    @Override
    protected ClassFormInfos[] refreshInfo() {
        try {
            ClassFormInfos[] classFormInfos = new ClassFormInfos[7];
            for (int i = 0; i < 7; i++) {
                String s = new IOForInfo(new File(getInfoBasicFile(), (i + 1) + ".json")).getInfos();
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<ClassFormInfo> infos = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    infos.add(new ClassFormInfo(
                            jsonObject.getString("class"),
                            jsonObject.getString("time")));
                }
                classFormInfos[i] = new ClassFormInfos(infos);
            }
            return classFormInfos;
        } catch (Exception e) {
            Log.err.systemPrint(CFInfoControl.class, "ClassFormInfos.json文件读取失败", e);
        }
        ArrayList<ClassFormInfo> temp = new ArrayList<>();

        temp.add(new ClassFormInfo("无", "00:00-00:00"));

        return new ClassFormInfos[]{
                new ClassFormInfos(temp), //1
                new ClassFormInfos(temp), //2
                new ClassFormInfos(temp), //3
                new ClassFormInfos(temp), //4
                new ClassFormInfos(temp), //5
                new ClassFormInfos(temp), //6
                new ClassFormInfos(temp)
                //7
        };
    }

    public record nextClassInfo(String time, String className) {
    }
}
