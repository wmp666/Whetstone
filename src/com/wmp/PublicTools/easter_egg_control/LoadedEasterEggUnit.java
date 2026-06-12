package com.wmp.PublicTools.easter_egg_control;

import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;

import java.util.Arrays;

public record LoadedEasterEggUnit(BasicEasterEggUnit easterEggUnit, String[] args) {
    @Override
    public String toString() {
        return String.format("初始化后的彩蛋单元{彩蛋单元：%s, 要传入的参数：%s}", easterEggUnit, Arrays.toString(args));
    }
}
