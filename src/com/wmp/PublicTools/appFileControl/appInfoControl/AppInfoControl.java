package com.wmp.PublicTools.appFileControl.appInfoControl;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class AppInfoControl extends CTInfoControl<AppInfo> {
    @Override
    public File getInfoBasicFile() {
        return new File(CTInfo.DATA_PATH, "appFileInfo.json");
    }

    @Override
    public void setInfo(AppInfo appInfo) {
        JSONObject jsonObject = new JSONObject();

        if (appInfo.messageShowTime() != -1) {
            jsonObject.put("SSMDWaitTime", appInfo.messageShowTime());
        }
        jsonObject.put("joinInsiderProgram", appInfo.joinInsiderProgram());

        IOForInfo io = new IOForInfo(getInfoBasicFile());
        try {
            io.setInfo(jsonObject.toString());
        } catch (IOException e) {
            Log.err.print(getClass(), "保存应用信息失败", e);
        }
    }

    @Override
    protected AppInfo refreshInfo() {

        try {
            IOForInfo io = new IOForInfo(getInfoBasicFile());
            String info = io.getInfos();
            if (!info.equals("err")) {
                JSONObject jsonObject = new JSONObject(info);
                return
                        new AppInfo(jsonObject.optInt("SSMDWaitTime", 5),
                                jsonObject.optBoolean("joinInsiderProgram", false));
            }
        } catch (IOException e) {
            Log.err.print(getClass(), "获取应用信息失败", e);
        }
        return new AppInfo(5, false);
    }
}
