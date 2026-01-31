package com.wmp;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.StartupParameters;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.SwingRun;

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
    public static final String version = "1.3.0";

    private static final TreeMap<String, StartupParameters> allArgs = new TreeMap<>();
    public static ArrayList<String> argsList = new ArrayList<>();

    static {
        //加载基础目录

        allArgs.put("StartUpdate:false", StartupParameters.creative("-StartUpdate:false", "/StartUpdate:false"));
        allArgs.put("屏保:展示", StartupParameters.creative("/s", "-s"));
        allArgs.put("screenProduct:view", StartupParameters.creative("/p", "-p"));

        allArgs.put("CTInfo:isError", StartupParameters.creative("/CTInfo:error", "-CTInfo:error"));
        allArgs.put("BasicDataPath", StartupParameters.creative("/BasicDataPath", "-BasicDataPath"));
        allArgs.put("EasterEgg:notShow", StartupParameters.creative("/EasterEgg:notShow", "-EasterEgg:notShow"));
    }

    public static void main(String[] args) {
        System.out.println("版本：" + version);
        if (args.length > 0) {
            argsList = new ArrayList<>(Arrays.asList(args));
            System.out.println("使用的启动参数:" + Arrays.toString(args));
        }

        CTInfo.init();

        try {
            SwingRun.show();
        } catch (Exception e) {
            Log.err.print(Main.class, "窗口初始化失败", e);
        }


        Log.info.print("Main", "初始化完毕");


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