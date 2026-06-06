package com.wmp.PublicTools.easter_egg_control;

public abstract class BasicEasterEggUnit {
    public abstract String getID();
    public abstract String getVersion();

    /**
     * 用于获取开发彩蛋时所用的磨刀石开发版本
     * @return 版本号
     */
    public abstract String getTargetVersion();

    public abstract String help();
    public abstract void run(String[] args);
    public abstract void clear();

    @Override
    public String toString() {
        return String.format("彩蛋单元{ID:%s, Version:%s, TargetVersion:%s}", getID(), getVersion(), getTargetVersion());
    }
}
