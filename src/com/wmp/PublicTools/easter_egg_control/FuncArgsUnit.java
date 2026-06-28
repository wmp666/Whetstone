package com.wmp.PublicTools.easter_egg_control;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 一个方法的名称,参数单元
 * @param funcName 名称
 * @param args 参数
 */
public record FuncArgsUnit(String funcName,@NotNull FuncArgUnit[] args) {
    @Override
    public @NotNull String toString() {
        return "FuncArgsUnit{" + "funcName='" + funcName + '\'' + ", args=" + Arrays.toString(args) + '}';
    }
}
