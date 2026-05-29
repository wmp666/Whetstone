package com.wmp.PublicTools;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.wmp.PublicTools.printLog.Log;

public interface DrawingRights extends Library {
    DrawingRights INSTANCE = Native.load(CTInfo.TEMP_PATH + "\\Whetstone\\DrawingRights.dll", DrawingRights.class);

    boolean RunAsAdmin(String exePath, String params);

    class Tools{
        public static boolean isAdmin(){
            try {
                for (Advapi32Util.Account group : Advapi32Util.getCurrentUserGroups()) {
                    // 使用Windows官方SID "S-1-5-32-544" 进行判断，最可靠
                    if ("S-1-5-32-544".equals(group.sidString)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                Log.err.print(com.wmp.PublicTools.DrawingRights.Tools.class, "判断管理员权限失败", e);
                return false; // 发生任何异常，均视为无管理员权限
            }
        }
    }

}

