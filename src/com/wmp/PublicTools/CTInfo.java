package com.wmp.PublicTools;

import com.wmp.Main;
import com.wmp.PublicTools.appFileControl.IconControl;
import com.wmp.PublicTools.appFileControl.appInfoControl.AppInfo;
import com.wmp.PublicTools.appFileControl.appInfoControl.AppInfoControl;
import com.wmp.PublicTools.easter_egg_control.BasicEasterEggUnit;
import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.LoadedEasterEggUnit;
import com.wmp.PublicTools.io.GetPath;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.recording.tools.GetRecordingInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sound.sampled.Mixer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String DEVELOP_VERSION = Main.developVersion;

    public static List<BasicEasterEggUnit> easterEggUnits = EasterEggControl.installAll(false);
    public static Map<String, List<LoadedEasterEggUnit>> EEMap = new HashMap<>();

    public static AppInfo appInfo = new AppInfo(5, false);

    static {
        initCTRunImportInfo();
    }

    public static void init() {

        initCTRunImportInfo();

        initCTBasicInfo();

        initEEInfo();

        List<Mixer.Info> infos = GetRecordingInfo.enumerateInputDevices();
        StringBuilder sb = new StringBuilder();
        infos.forEach(info ->{
            sb.append(info.getName()).append("\n");
        });
        try {
            File file = new File(TEMP_PATH, "Whetstone\\Recording\\MixerInfos.txt");
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(file.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initEEInfo() {
        //获取原始Json数据
        JSONObject EEUnitsJson = new JSONObject();
        JSONObject varJson = new JSONObject();

        File varFile = new File(GetPath.getAppPath(GetPath.SOURCE_FILE_PATH), "var_list.json");
        if (varFile.exists()) {
            varJson = new JSONObject(IOForInfo.getInfos(varFile.getAbsolutePath()));
        }

        File startList = new File(GetPath.getAppPath(GetPath.SOURCE_FILE_PATH), "start_list.json");
        if (startList.exists()) {
            EEUnitsJson = new JSONObject(IOForInfo.getInfos(startList.getAbsolutePath()));
        }

        //进一步处理原数据
        //1.变量列表
        Map<String, JSONObject> varMap = new HashMap<>();
        varJson.toMap().forEach((key, value) -> {
            if (value instanceof Map<?,?>) {
                varMap.put(key, new JSONObject((Map<?, ?>) value));
            }
        });
        //2.彩蛋列表

        EEMap.put("app_start",
                List.of(EasterEggControl.getLoadedEasterEggUnits(EEUnitsJson.optJSONArray("app_start"), varMap, easterEggUnits)));
        EEMap.put("class_start",
                List.of(EasterEggControl.getLoadedEasterEggUnits(EEUnitsJson.optJSONArray("class_start"), varMap, easterEggUnits)));
        try {
            EEUnitsJson.optJSONObject("class_list").toMap().forEach((key, value) -> {
                if (value instanceof JSONArray) {
                    EEMap.put(key,
                            List.of(EasterEggControl.getLoadedEasterEggUnits((JSONArray) value, varMap, easterEggUnits)));
                }else if (value instanceof ArrayList<?>){
                    EEMap.put(key,
                            List.of(EasterEggControl.getLoadedEasterEggUnits(new JSONArray((ArrayList<?>) value), varMap, easterEggUnits)));

                }
            });
        } catch (Exception e) {
            Log.err.print(CTInfo.class, "加载彩蛋列表失败", e);
        }
        //3.输出数据
        Log.info.print(CTInfo.class.toString(), "彩蛋列表:" + EEMap);
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
