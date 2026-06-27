package com.wmp.PublicTools.easter_egg_control;

import org.jetbrains.annotations.NotNull;

/**
 * 一个方法的名称,参数单元
 * @param funcName 名称
 * @param args 参数
 */
public record FuncArgsUnit(String funcName,@NotNull FuncArgUnit[] args) {
}
