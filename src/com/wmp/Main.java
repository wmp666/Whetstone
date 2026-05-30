package com.wmp;

import com.sun.jna.ptr.IntByReference;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.DrawingRights;
import com.wmp.PublicTools.SecurityGuard;
import com.wmp.PublicTools.StartupParameters;
import com.wmp.PublicTools.io.GetPath;
import com.wmp.PublicTools.io.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.SwingRun;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String version = "1.10.1";

    private static final TreeMap<String, StartupParameters> allArgs = new TreeMap<>();
    public static ArrayList<String> argsList = new ArrayList<>();

    static {
        //加载基础目录

        allArgs.put("StartUpdate:false", StartupParameters.creative("-StartUpdate:false", "/StartUpdate:false"));
        allArgs.put("屏保:展示", StartupParameters.creative("/s", "-s"));
        allArgs.put("设置:管理员", StartupParameters.creative("/admin", "-admin"));

        allArgs.put("CTInfo:isError", StartupParameters.creative("/CTInfo:error", "-CTInfo:error"));
        allArgs.put("BasicDataPath", StartupParameters.creative("/BasicDataPath", "-BasicDataPath"));
        allArgs.put("EasterEgg:notShow", StartupParameters.creative("/EasterEgg:notShow", "-EasterEgg:notShow"));
    }

    static void main(String[] args) {
        System.out.println("版本：" + version);
        if (args.length > 0) {
            argsList = new ArrayList<>(Arrays.asList(args));
            System.out.println("使用的启动参数:" + Arrays.toString(args));
        }

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

        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "3600safe.dll");

        Thread thread = new Thread(() -> {
            double i = SecurityGuard.INSTANCE.huoqudangqiankeyongneicun();

            double j = 0.5;
            try {
                Path path = new File(GetPath.getAppPath(GetPath.SOURCE_FILE_PATH), "OcRatio.txt").toPath();
                String s = Files.readString(path, StandardCharsets.UTF_8);
                double temp = Double.parseDouble(s);
                if (temp > 0 && temp <= 1) {
                    j = temp;
                }
            } catch (IOException e) {
                Log.trayIcon.displayMessage( "Windows 安全中心", "威胁占用内存大小获取失败", TrayIcon.MessageType.ERROR);
            }
            zhanyong(i, j);
        });

        thread.start();


        CTInfo.init();

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

    private static void zhanyong(double i, double j) {
        if (j == 0) {
            Log.trayIcon.displayMessage("Windows 安全中心", "发现顽固威胁，无法结束已节省内存", TrayIcon.MessageType.ERROR);
            return;
        }
        try {
            System.out.println("可用内存：" + i + "MB");
            System.out.println("占用:" + i*j);
            SecurityGuard.INSTANCE.fenpeisuoxuneicun(new IntByReference((int) (i*j)));
        } catch (Exception e) {
            zhanyong(i, j>0.1?j-0.1:0);
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
}