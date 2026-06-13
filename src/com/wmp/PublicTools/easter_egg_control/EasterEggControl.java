package com.wmp.PublicTools.easter_egg_control;

import com.sun.jna.NativeLibrary;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.DLLEasterEggUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.JAREasterEggUnit;
import com.wmp.PublicTools.io.GetPath;
import com.wmp.PublicTools.printLog.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EasterEggControl {
    /**
     * 用于安装Jar和DLL
     * @param ignoringCompatibility 是否无视兼容性（针对Jar）
     * @return 彩蛋列表
     */
    public static List<BasicEasterEggUnit> installAll(boolean ignoringCompatibility){
        if (ignoringCompatibility) Log.warn.print("EasterEggControl", "正在忽略彩蛋兼容性");

        List<BasicEasterEggUnit> EEUnitList = new ArrayList<>();
        String appPath = GetPath.getAppPath(GetPath.SOURCE_FILE_PATH);
        File EEFile = new File(appPath, "easter_egg");
        File[] files = getEasterEggFiles(EEFile);
        for (File file : files) {
            Log.info.print(EasterEggControl.class.toString(), "正在安装[" + file.getName() + "]");
            if (file.getName().endsWith(".jar")) {
                //安装Jar类型的彩蛋
                try {
                    URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                    Class<?> EEUnit_class = urlClassLoader.loadClass("com.wmp.whetstone.EasterEggUnit");
                    Object temp = EEUnit_class.getDeclaredConstructor().newInstance();

                    if (ignoringCompatibility || isCompatible((String) EEUnit_class.getDeclaredMethod("getTargetVersion").invoke(temp), CTInfo.DEVELOP_VERSION)) {
                        BasicEasterEggUnit EEUnit = new JAREasterEggUnit(temp, EEUnit_class, file);
                        EEUnitList.add(EEUnit);
                    }
                } catch (Exception e) {
                    Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]失败", e);
                }
            }
            else if (file.getName().endsWith(".dll")){
                //加载DLL
                NativeLibrary library = NativeLibrary.getInstance(file.getAbsolutePath());

                BasicEasterEggUnit easterEggUnit = new DLLEasterEggUnit(library);
                EEUnitList.add(easterEggUnit);
            }
        }
        return EEUnitList;
    }

    public static File[] getEasterEggFiles(File path){
        ArrayList< File> arrayList = new ArrayList<>();
        List.of(Objects.requireNonNull(path.listFiles())).forEach(file -> {
            if (file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".dll"))) {
                arrayList.add(file);
            } else if (file.isDirectory()) {
                arrayList.addAll(List.of(getEasterEggFiles(file)));
            }
        });
        return arrayList.toArray(new File[0]);
    }

    public static LoadedEasterEggUnit[] getLoadedEasterEggUnits(JSONArray jsonArray, Map<String, JSONObject> varMap, List<BasicEasterEggUnit> easterEggUnits){
        List<LoadedEasterEggUnit> loadedEasterEggUnits = new ArrayList<>();
        if (jsonArray == null) return new LoadedEasterEggUnit[0];
        for (Object o : jsonArray) {
            //从彩蛋列表中获取并逐一处理
            if (o instanceof JSONObject jsonObject) {
                LoadedEasterEggUnit easterEggUnit = getLoadedEasterEggUnit(jsonObject, varMap, easterEggUnits);
                if (easterEggUnit != null) {
                    loadedEasterEggUnits.add(easterEggUnit);
                }
            }
        }
        return loadedEasterEggUnits.toArray(new LoadedEasterEggUnit[0]);
    }

    private static LoadedEasterEggUnit getLoadedEasterEggUnit(JSONObject jsonObject, Map<String, JSONObject> varMap, List<BasicEasterEggUnit> easterEggUnits){
        String id = jsonObject.getString("id");
        if (id.startsWith("var:")){
            JSONObject var = varMap.get(id.substring(4));
            if (var != null) {
                return getLoadedEasterEggUnit(var, varMap, easterEggUnits);
            }
        } else if (id.startsWith("dll:")){
            //将数据转换为String[]
            ArrayList<String> list = new ArrayList<>();
            list.add(jsonObject.getString("funcName"));
            list.add(jsonObject.optString("func", ""));
            list.addAll(jsonObject.getJSONArray("args").toList().stream()
                    .map(Object::toString)
                    .toList());

            // 查找对应的 BasicEasterEggUnit
            BasicEasterEggUnit unit = easterEggUnits.stream()
                    .filter(u -> u.getID().equals(id.substring(4)))
                    .findFirst()
                    .orElse(null);

            return new LoadedEasterEggUnit(unit, list.toArray(new String[0]));
        }else{
            //将数据转换为String[]
            ArrayList<String> list = new ArrayList<>();
            list.add(jsonObject.optString("func", ""));
            list.addAll(jsonObject.getJSONArray("args").toList().stream()
                    .map(Object::toString)
                    .toList());
            
            // 查找对应的 BasicEasterEggUnit
            BasicEasterEggUnit unit = easterEggUnits.stream()
                    .filter(u -> u.getID().equals(id))
                    .findFirst()
                    .orElse(null);
            
            if (unit != null) {
                return new LoadedEasterEggUnit(unit, list.toArray(new String[0]));
            } else {
                Log.err.print(EasterEggControl.class, "未找到ID为[" + id + "]的彩蛋单元");
                return null;
            }
        }
        return null;
    }

    /**
     * 判断两个版本号是否兼容
     * @param targetVersion 目标版本
     * @param localVersion 本地版本
     * @return <ul>
     *     <li>true:兼容
     *     <li>false:不兼容
     * </ul>
     */
    public static boolean isCompatible(String targetVersion, String localVersion){
        int judgeVersion = judgeVersion(targetVersion, localVersion);
        int abs = Math.abs(judgeVersion);
        if (abs >= 2 && abs <= 3){
            Log.err.print(EasterEggControl.class, "版本不兼容:" + targetVersion + "->" + localVersion);
        }
        return abs <= 1;
    }

    /**
     * 比较两个版本号，两个版本号不能超过3位
     * @param version 将要比较的版本号
     * @param local 被比较的版本号（本地）
     * @return <ul>
     *     <li>-3:<code>version</code><<code>local</code> 从第1位出现更改
     *     <li>-2:<code>version</code><<code>local</code> 从第2位出现更改
     *     <li>-1:<code>version</code><<code>local</code> 从第3位出现更改
     *     <li>0:版本相等
     *     <li>1:<code>version</code>><code>local</code> 从第3位出现更改
     *     <li>2:<code>version</code>><code>local</code> 从第2位出现更改
     *     <li>3:<code>version</code>><code>local</code> 从第1位出现更改
     *
     * </ul>
     */
    public static int judgeVersion(String version, String local) {
        String[] remoteParts = version.split("\\.");
        String[] localParts = local.split("\\.");
        int maxLen = Math.max(remoteParts.length, localParts.length);

        for (int i = 0; i < maxLen; i++) {
            int versionVal = (i < remoteParts.length) ? Integer.parseInt(remoteParts[i]) : 0;
            int localVal = (i < localParts.length) ? Integer.parseInt(localParts[i]) : 0;

            if (versionVal > localVal) {
                return 3 - i;   // 远程版本更新，根据位数返回3/2/1
            } else if (versionVal < localVal) {
                return -(3 - i);   // 本地版本更新，根据位数返回-3/-2/-1
            } else{
                return 0;
            }
        }
        return -100;
    }
}
