package com.wmp.PublicTools.easter_egg_control.easterEggUnit;

import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;

public abstract class BasicEasterEggUnit {
    public abstract String getID();

    public abstract String getVersion();

    /**
     * 用于获取开发彩蛋时所用的磨刀石开发版本
     *
     * @return 版本号
     */
    public abstract String getTargetVersion();

    public abstract String help();

    /**
     * 获取方法帮助,若没有则会返回null
     *
     * @return 方法帮助
     */
    public FuncHelpUnit[] funcHelps() {
        return null;
    }

    public abstract void run(String[] args);

    public abstract void clear(String funcName);

    @Override
    public String toString() {
        return String.format("彩蛋单元{ID:%s, Version:%s, TargetVersion:%s}", getID(), getVersion(), getTargetVersion());
    }
}
