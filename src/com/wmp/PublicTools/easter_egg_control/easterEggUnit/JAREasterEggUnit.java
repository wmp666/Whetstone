package com.wmp.PublicTools.easter_egg_control.easterEggUnit;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.printLog.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class JAREasterEggUnit extends BasicEasterEggUnit {
    private final Object temp;
    private final Class<?> EEUnit_class;
    private final File file;

    private boolean isWhile = true;

    public JAREasterEggUnit(Object temp, Class<?> EEUNIT_class, File file) {
        this.EEUnit_class = EEUNIT_class;
        this.temp = temp;
        this.file = file;

    }

    /**
     * 调用JAR中的方法
     * 功能：已";"分割
     * @param args 方法的参数 内容[功能, 传入参数...]
     */
    public void run(String[] args) {
        List<String> funcList = getFuncList(args[0]);
        //将传入的参数转换为对应类型
        ArrayList<Object> inArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        Log.info.print(DLLEasterEggUnit.class.toString(), String.format("正在调用JAR中的方法|功能：%s|参数：%s", funcList, inArgs));


        AtomicLong sleep_before = new AtomicLong();
        AtomicLong sleep_while = new AtomicLong();
        AtomicLong sleep_after = new AtomicLong();

        funcList.forEach(func -> {
            if (func.startsWith("sleep:before:")) {
                sleep_before.set(Long.parseLong(func.split(":")[2]));
            } else if (func.startsWith("sleep:while:")) {
                sleep_while.set(Long.parseLong(func.split(":")[2]));
            } else if (func.startsWith("sleep:after:")) {
                sleep_after.set(Long.parseLong(func.split(":")[2]));
            }
        });

        try {
            Thread.sleep(sleep_before.get());

            if (funcList.contains("while")){
                while (isWhile){
                    EEUnit_class.getDeclaredMethod("run", String[].class).invoke(temp, (Object) args);
                    Thread.sleep(sleep_while.get());
                }
            }else if (funcList.stream().anyMatch(func -> func.startsWith("for:"))){
                int count = Integer.parseInt(funcList.stream().filter(func -> func.startsWith("for:")).findFirst().get().split(":")[1]);
                for (int i = 0; i < count; i++) {
                    EEUnit_class.getDeclaredMethod("run", String[].class).invoke(temp, (Object) args);
                    Thread.sleep(sleep_while.get());
                }
            } else {
                EEUnit_class.getDeclaredMethod("run", String[].class).invoke(temp, (Object) args);
            }


            Thread.sleep(sleep_after.get());
        } catch (Exception e) {
            Log.err.print(DLLEasterEggUnit.class, "调用[" + file.getName() + "]的[run]方法失败", e);
        }

    }

    /**
     * 获取功能列表
     * @param func 功能
     *             <ul>
     *             <li>while 循环</li>
     *             <li>for:[count] 循环[count]次</li>
     *             <li>sleep:before:[time] 在启动前休眠[time]毫秒</li>
     *             <li>sleep:after:[time] 在启动后休眠[time]毫秒</li>
     *             <li>sleep:while:[time] 在循环时的间隔休眠[time]毫秒（存在循环时可用）</li>
     *             </ul>
     * @return 功能列表
     */
    private List<String> getFuncList(String func){
        String[] split = func.split(";");
        List< String> list = new ArrayList<>();
        Collections.addAll(list, split);
        return list;
    }


    @Override
    public String getID() {
        try {
            return (String) EEUnit_class.getDeclaredMethod("getID").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[getID]方法失败", e);
            return null;
        }
    }

    @Override
    public String getVersion() {
        try {
            return (String) EEUnit_class.getDeclaredMethod("getVersion").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[getVersion]方法失败", e);
            return null;
        }
    }

    @Override
    public String getTargetVersion() {
        try {
            return (String) EEUnit_class.getDeclaredMethod("getTargetVersion").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[getTargetVersion]方法失败", e);
            return null;
        }
    }

    @Override
    public String help() {
        try {
            return (String) EEUnit_class.getDeclaredMethod("help").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[help]方法失败", e);
            return null;
        }
    }

    @Override
    public FuncHelpUnit[] funcHelps() {
        try {
            return (FuncHelpUnit[]) EEUnit_class.getDeclaredMethod("funcHelps").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[funcHelps]方法失败", e);
            return null;
        }
    }

    @Override
    public void clear() {
        isWhile = false;

        try {
            EEUnit_class.getDeclaredMethod("clear").invoke(temp);
        } catch (Exception e) {
            Log.err.print(EasterEggControl.class, "安装[" + file.getName() + "]的[clear]方法失败", e);
        }
    }
}