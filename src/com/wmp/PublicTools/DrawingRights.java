package com.wmp.PublicTools;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.wmp.PublicTools.printLog.Log;

import java.io.File;

public interface DrawingRights extends Library {
    DrawingRights INSTANCE = Native.load(CTInfo.TEMP_PATH + "\\Whetstone\\DrawingRights.dll", DrawingRights.class);

    int RunAsAdmin(WString exePath, WString params);

    class Tools {
        public static boolean RunAsAdmin(String exePath, String params) {
            Log.info.systemPrint("DrawingRights", "RunAsAdmin: " + exePath + " " + params);
            return DrawingRights.INSTANCE.RunAsAdmin(new WString(exePath), new WString(params)) == 1;
        }

        public static boolean isAdmin() {
            File testDir = new File(System.getenv("ProgramFiles"), "test_permission");
            try {

                return testDir.mkdirs();
            } catch (SecurityException e) {
                return false;
            } finally {
                testDir.delete();
            }
        }
    }

}

