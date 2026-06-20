package com.wmp.PublicTools.easter_egg_control.easterEggUnit;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.var.Var;
import com.wmp.PublicTools.printLog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class DLLEasterEggUnit extends BasicEasterEggUnit {

    private final NativeLibrary dll;

    private boolean isWhile = true;

    public DLLEasterEggUnit(NativeLibrary dll) {
        this.dll = dll;
    }

    /**
     * 调用DLL中的方法
     * 功能：已";"分割
     *
     * @param args 方法的参数 内容[方法名, 功能, 传入参数...]
     */
    public void run(String[] args) {
        Log.info.print(DLLEasterEggUnit.class.toString(), "正在调用DLL中的方法...");

        Function function = dll.getFunction(args[0]);
        ArrayList<Object> inArgs = new ArrayList<>();
        //将传入的参数转换为对应类型
        for (int i = 2; i < args.length; i++) {
            inArgs.add(Var.StringToVar(args[i]).toTargetStyle());
        }
        List<String> funcList = getFuncList(args[1]);
        Log.info.print(DLLEasterEggUnit.class.toString(), String.format("正在调用DLL中的方法：%s|功能：%s|参数：%s", args[0], funcList, inArgs));

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

            if (funcList.contains("while")) {
                while (isWhile) {
                    function.invokeVoid(inArgs.toArray());
                    Thread.sleep(sleep_while.get());
                }
            } else if (funcList.stream().anyMatch(func -> func.startsWith("for:"))) {
                int count = Integer.parseInt(funcList.stream().filter(func -> func.startsWith("for:")).findFirst().get().split(":")[1]);
                for (int i = 0; i < count; i++) {
                    function.invokeVoid(inArgs.toArray());
                    Thread.sleep(sleep_while.get());
                }
            } else {
                function.invokeVoid(inArgs.toArray());
            }


            Thread.sleep(sleep_after.get());
        } catch (Exception e) {
            Log.err.print(DLLEasterEggUnit.class, "调用DLL中的方法失败", e);
        }
    }

    /**
     * 获取功能列表
     *
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
    private List<String> getFuncList(String func) {
        String[] split = func.split(";");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        return list;
    }


    @Override
    public String getID() {
        try {
            return dll.getFunction("getID").invokeString(new Object[0], false);
        } catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "dll作者未写该方法\n" + e);
            return dll.getFile().getName();
        }
    }

    @Override
    public String getVersion() {
        try {
            return dll.getFunction("getVersion").invokeString(new Object[0], false);
        } catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "dll作者未写该方法\n" + e);
            return "获取失败";
        }
    }

    @Override
    public String getTargetVersion() {
        return CTInfo.DEVELOP_VERSION;
    }

    @Override
    public String help() {
        try {
            return dll.getFunction("help").invokeString(new Object[0], false);
        } catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "dll作者未写该方法\n" + e);
            return "无";
        }
    }

    @Override
    public FuncHelpUnit[] funcHelps() {
        try {
            Function function = dll.getFunction("funcHelps");
            String s = function.invokeString(new Object[0], false);
            Log.info.print(DLLEasterEggUnit.class.toString(), "获取的方法帮助：" + s);
            String[] split = s.split(";");
            ArrayList<FuncHelpUnit> funcHelpUnits = new ArrayList<>();
            //FuncHelpUnit[] funcHelpUnits = new FuncHelpUnit[split.length];
            for (int i = 0; i < split.length; i++) {
                String[] split1 = split[i].split("\\|", 2);
                if (split1.length == 2) funcHelpUnits.add(new FuncHelpUnit(split1[0], split1[1]));
            }
            Log.info.print(DLLEasterEggUnit.class.toString(), "获取的方法帮助(Array)：" + funcHelpUnits);
            return funcHelpUnits.toArray(new FuncHelpUnit[0]);
        } catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "dll作者未写该方法\n" + e);
        }
        return null;
    }

    @Override
    public void clear(String funcName) {
        isWhile = false;

        try {
            dll.getFunction("clear").invokeVoid(new Object[]{funcName});
        } catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "调用[clear]方法失败\n"+e);
        }
    }
}