package com.wmp.PublicTools.easter_egg_control;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.printLog.Log;

import java.util.ArrayList;

public class DLLEasterEggUnit extends BasicEasterEggUnit {

    private final NativeLibrary dll;

    public DLLEasterEggUnit(NativeLibrary dll) {
        this.dll = dll;
    }

    /**
     * 调用DLL中的方法
     *
     * @param args 方法的参数 内容[方法名, 传入参数...]
     */
    public void run(String[] args) {
        Log.info.print(DLLEasterEggUnit.class.toString(), "正在调用DLL中的方法...");

        Function function = dll.getFunction(args[0]);
        ArrayList<Object> inArgs = new ArrayList<>();
        //将传入的参数转换为对应类型
        for (int i = 1; i < args.length; i++) {
            inArgs.add(DLLVar.StringToVar(args[i]).toTargetStyle());
        }
        Log.info.print(DLLEasterEggUnit.class.toString(), String.format("正在调用DLL中的方法：%s|参数：%s", args[0], inArgs));
        function.invokeVoid(inArgs.toArray());
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
    public void clear() {
        try {
            dll.getFunction("clear").invokeVoid(new Object[0]);
        }catch (Error | Exception e) {
            //Log.err.print(DLLEasterEggUnit.class, "调用[clear]方法失败\n"+e);
        }
    }
}