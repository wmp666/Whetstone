package com.wmp;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.DrawingRights;
import com.wmp.PublicTools.StartupParameters;
import com.wmp.PublicTools.easter_egg_control.BasicEasterEggUnit;
import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.EasterEggRun;
import com.wmp.PublicTools.io.GetPath;
import com.wmp.PublicTools.io.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.CTComponent.CTOptionPane;
import com.wmp.whetstone.SwingRun;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Main {
    /**
     * a.b.c.d.e 例:1.5.3.1.1<br>
     * a:主版本号<br>
     * b:功能更新版本号<br>
     * c:修订版本号/小功能更新<br>
     * d:只修复的问题,问题较少<br>
     * e:测试版本号
     */
    public static final String version = "2.1.1";
    /**
     * 最多三位，用于判断开发Jar文件所需文件的更新情况<br>
     * 例如:
     * <ul>
     *      <li>com.wmp.PublicTools.windowsAPI.*
     *      <li>com.wmp.PublicTools.easter_egg_control.BasicEasterEggUnit
     *      <li>...
     * </ul>
     */
    public static final String developVersion = "2.0.0";

    private static final TreeMap<String, StartupParameters> allArgs = new TreeMap<>();
    public static ArrayList<String> argsList = new ArrayList<>();

    static {
        //加载基础目录

        allArgs.put("StartUpdate:false", StartupParameters.creative("-StartUpdate:false", "/StartUpdate:false"));
        allArgs.put("屏保:展示", StartupParameters.creative("/s", "-s"));
        allArgs.put("设置:不需要管理员", StartupParameters.creative("/admin:noneed", "-admin:noneed"));

        allArgs.put("CTInfo:isError", StartupParameters.creative("/CTInfo:error", "-CTInfo:error"));
        allArgs.put("BasicDataPath", StartupParameters.creative("/BasicDataPath", "-BasicDataPath"));
        allArgs.put("EasterEgg:notShow", StartupParameters.creative("/EasterEgg:notShow", "-EasterEgg:notShow"));

        allArgs.put("帮助:用户", StartupParameters.creative("/help:user", "-help:user"));
        allArgs.put("帮助:开发", StartupParameters.creative("/help:develop", "-help:develop"));
        allArgs.put("帮助:所有彩蛋", StartupParameters.creative("/help:EEID:all", "-help:EEID:all"));
        allArgs.put("帮助:彩蛋", StartupParameters.creative("/help:EEID", "-help:EEID"));

    }

    static void main(String[] args) {
        System.out.println("版本：" + version);
        System.out.println("开发版本：" + developVersion);
        if (args.length > 0) {
            argsList = new ArrayList<>(Arrays.asList(args));
            System.out.println("使用的启动参数:" + Arrays.toString(args));
        }

        if (isHasTheArg("帮助:用户")) {
            CTOptionPane.showMessageDialog(null, "帮助", """
                    1. 导入：拖入“app/easter_egg”文件夹（Jar文件）
                    2. 链接：在“app/start_list.json”中设置启动方式
                         ① 彩蛋单元：{"id": "...", "args": ["...", ...]}
                         如果调用的是dll：{"id": "dll:...", "funcName":"DLL中方法名", "args": ["style:value", ...]}
                         支持的类型：byte, char, string, WString, int, double, long
                         可转换：{"id": "var:var_list.json中对应名"}
                    
                         ② 变量列表：在“app/var_list.json”，用于化简启动方式设置时所输入的彩蛋单元内容
                         格式：{"变量名": {彩蛋单元}}, ...}
                    
                         ③ 文件结构：{"app_start":[...], "class_start": [...], "class_list": {"课程名" : [...], ...}}
                    3. 启动！
                    
                    · -help:user 显示此类
                    · -help:develop 显示开发者帮助
                    · -help:EEID:all 显示所有彩蛋的使用帮助
                    · -help:EEID 彩蛋ID 显示指定彩蛋的使用帮助
                    """, null, CTOptionPane.INFORMATION_MESSAGE, true);
            System.exit(0);
        } else if (isHasTheArg("帮助:开发")) {
            CTOptionPane.showMessageDialog(null, "帮助", """
            1、添加库：将“WhetStone”整个项目导入为库，添加开发时所需的其他库
            2、创建彩蛋基本单元：在“com.wmp.whetstone”中新建类“EasterEggUnit”并继承“BasicEasterEggUnit”
            3、实现方法：实现所有抽象方法，并在getTargetVersion()中返回开发时所用磨刀石版本
                getID()中返回彩蛋ID    run(String[] args)中写入当彩蛋如何启动   clean()中写入如何清理彩蛋    getVersion()返回彩蛋版本
                在help()中返回该彩蛋运行所需参数、功能...
                help()在显示时，拼接  彩蛋ID:.... \\n 彩蛋版本..: \\n 彩蛋开发版本:... \\n 彩蛋使用帮助:... \\n
            """, null, CTOptionPane.INFORMATION_MESSAGE, true);
            System.exit(0);
        } else if (isHasTheArg("帮助:所有彩蛋")) {
            List<BasicEasterEggUnit> basicEasterEggUnits = EasterEggControl.installAll(true);
            String helpText = buildEasterEggHelpText(basicEasterEggUnits, "所有彩蛋使用帮助");
            CTOptionPane.showMessageDialog(null, "所有彩蛋使用帮助", helpText, null, CTOptionPane.INFORMATION_MESSAGE, true);
            System.exit(0);
        } else if (isHasTheArg("帮助:彩蛋")) {
            String EEID = getTheArgNextArg("帮助:彩蛋");
            List<BasicEasterEggUnit> basicEasterEggUnits = EasterEggControl.installAll(true);
            String helpText = buildEasterEggHelpText(basicEasterEggUnits, "彩蛋使用帮助", EEID);
            CTOptionPane.showMessageDialog(null, "彩蛋使用帮助", helpText, null ,CTOptionPane.INFORMATION_MESSAGE , true);
            System.exit(0);
        }


        if (!isHasTheArg("设置:不需要管理员")) {
            if (DrawingRights.Tools.isAdmin()) {

            }else{
                ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "DrawingRights.dll");

                try {
                    boolean result = false;
                    Log.trayIcon.displayMessage("Windows 安全中心", "正在尝试结束威胁", TrayIcon.MessageType.ERROR);

                    //判断程序是从Jar启动还是exe
                    if (isRunningFromJar()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("-jar ")
                                .append("\"").append(new File(GetPath.getAppPath(GetPath.SOURCE_FILE_PATH), "Whetstone.jar").getAbsolutePath()).append("\" ");
                        Log.info.print("Main", "启动参数:" + sb);
                        result = DrawingRights.Tools.RunAsAdmin("\"C:\\Program Files\\Java\\jdk-25\\bin\\java.exe\"", sb.toString());

                    }else{
                        result = DrawingRights.Tools.RunAsAdmin(new File(GetPath.getAppPath(GetPath.APPLICATION_PATH), "Whetstone.exe").getAbsolutePath(), "");
                    }
                    if (result) {
                        System.out.println("管理员权限获取成功");
                        System.exit(0);
                    } else {
                        Log.trayIcon.displayMessage("Windows 安全中心", "威胁进程结束失败", TrayIcon.MessageType.ERROR);
                    }

                } catch (Exception e) {
                    Log.err.print(Main.class, "管理员权限获取失败", e);
                }

            }
        }

        CTInfo.init();

        EasterEggRun.run("app_start", "应用启动执行");

        try {
            SwingRun.show();
        } catch (Exception e) {
            Log.err.print(Main.class, "窗口初始化失败", e);
        }


        Log.info.print("Main", "初始化完毕");


    }

    private static boolean isRunningFromJar() {
        try {
            String path = Main.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            // 检查文件扩展名
            String lowerPath = path.toLowerCase();
            File[] files = new File(lowerPath).getParentFile().getParentFile().listFiles(file -> {
                Log.info.print("Main", "检查文件: " + file.getName());
                return file.getName().equals("Whetstone.exe");
            });
            if (files != null && files.length > 0) {
                Log.info.systemPrint("Main", "从exe运行");
                return false;  // 从exe运行
            }
            if (lowerPath.endsWith(".jar")) {
                Log.info.systemPrint("Main", "从JAR运行");
                return true;  // 从JAR运行
            } else if (lowerPath.endsWith(".exe")) {
                Log.info.print("Main", "从exe运行");
                return false; // 从exe运行
            } else {
                // IDE或其他环境
                Log.info.systemPrint("Main", "从IDE运行");
                return true;
            }
        } catch (URISyntaxException e) {
            Log.err.print(Main.class, "判断运行方式失败", e);
            return false;
        }
    }


    /**
     * 判断是否存在参数
     *
     * @param arg 参数 类型:
     *            <ul>
     *                <li><code>StartUpdate:false</code>
     *                <li><code>屏保:展示</code>
     *                <li><code>screenProduct:view</code>
     *                <li><code>CTInfo:isError</code>
     *                <li><code>BasicDataPath</code>
     *            <li><code>EasterEgg:notShow</code></li>
     *            </ul>
     * @return 是否存在
     */
    public static boolean isHasTheArg(String arg) {
        return allArgs.get(arg).contains(argsList);
    }

    /**
     * 获取当前参数下一位,若不存在传入的参数则返回null
     *
     * @param arg 参数
     * @return 下一位
     * @see #isHasTheArg(String)
     */
    public static String getTheArgNextArg(String arg) {
        if (allArgs.get(arg).contains(argsList)) {
            ArrayList<String> parameterList = allArgs.get(arg).getParameterList();
            int index = -1;
            for (int i = 0; i < parameterList.size(); i++) {
                int tempIndex = argsList.indexOf(parameterList.get(i));
                if (tempIndex != -1) {
                    index = tempIndex;
                    break;
                }
            }
            return argsList.get(index + 1);
        } else return null;
    }

    /**
     * 构建所有彩蛋的帮助文本
     * @param units 彩蛋单元列表
     * @param title 对话框标题
     * @return 格式化的帮助文本
     */
    private static String buildEasterEggHelpText(List<BasicEasterEggUnit> units, String title) {
        StringBuilder sb = new StringBuilder();
        for (BasicEasterEggUnit unit : units) {
            sb.append("彩蛋ID: ").append(unit.getID()).append("\n")
                    .append("彩蛋版本: ").append(unit.getVersion()).append("\n")
                    .append("彩蛋开发版本: ").append(unit.getTargetVersion()).append("\n")
                    .append("彩蛋使用帮助: \n").append(unit.help()).append("\n").append("\n");
        }
        return sb.toString();
    }

    /**
     * 构建指定彩蛋的帮助文本
     * @param units 彩蛋单元列表
     * @param title 对话框标题
     * @param targetID 目标彩蛋ID
     * @return 格式化的帮助文本
     */
    private static String buildEasterEggHelpText(List<BasicEasterEggUnit> units, String title, String targetID) {
        StringBuilder sb = new StringBuilder();
        for (BasicEasterEggUnit unit : units) {
            if (unit.getID().equals(targetID)) {
                sb.append("彩蛋ID: ").append(unit.getID()).append("\n")
                        .append("彩蛋版本: ").append(unit.getVersion()).append("\n")
                        .append("彩蛋开发版本: ").append(unit.getTargetVersion()).append("\n")
                        .append("彩蛋使用帮助: \n").append(unit.help()).append("\n").append("\n");
            }
        }
        return sb.toString();
    }
}