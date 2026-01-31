package com.wmp.PublicTools;

import com.wmp.Main;
import com.wmp.PublicTools.appFileControl.IconControl;
import com.wmp.PublicTools.appFileControl.appInfoControl.AppInfo;
import com.wmp.PublicTools.appFileControl.appInfoControl.AppInfoControl;
import com.wmp.PublicTools.printLog.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CTInfo {
    public static double dpi = 1.0;
    public static int arcw = 1;
    public static int arch = 1;
    //数据位置
    public static String DATA_PATH;
    public static String TEMP_PATH;
    public static String APP_INFO_PATH;
    //基础数据
    public static String appName = "磨刀石";
    public static String author = "无名牌";
    public static String iconPath = "/image/icon/icon.png";

    public static String version = Main.version;

    public static AppInfo appInfo = new AppInfo(5, false);

    static {
        initCTRunImportInfo();
    }

    public static void init() {

        initCTRunImportInfo();

        initCTBasicInfo();

    }

    private static void initCTRunImportInfo() {

        //加载基础目录
        String path = System.getenv("LOCALAPPDATA");
        String s = Main.getTheArgNextArg("BasicDataPath");
        if (s == null) {
            if (path != null && !path.isEmpty()) {
                File file = new File(path, "\\ClassTools\\basicDataPath.txt");
                if (file.exists() && file.isFile()) {
                    try {
                        path = new File(Files.readString(file.toPath(), StandardCharsets.UTF_8)).getAbsolutePath();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else path = s;

        APP_INFO_PATH = path + "\\ClassToolsAppFile\\";
        DATA_PATH = path + "\\ClassTools\\";
        TEMP_PATH = path + "\\ClassToolsTemp\\";

    }

    private static void initCTBasicInfo() {

        appInfo = new AppInfoControl().getInfo();

        IconControl.init();
        Log.initTrayIcon();
    }
}
