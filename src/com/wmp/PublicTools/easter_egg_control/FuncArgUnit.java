package com.wmp.PublicTools.easter_egg_control;

/**
 * 方法的参数单元
 * @param type 类型
 * @param name 名称
 * @param help 帮助
 * @param isHasDefaultValue 是否有默认值
 * @param defaultValue 默认值
 */
public record FuncArgUnit(String type, String name, String help, boolean isHasDefaultValue, String defaultValue) {
    public static final String TYPE_STRING = "string";
    public static final String TYPE_INTEGER = "int";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_CHAR = "char";
    public static final String TYPE_WSTRING = "Wstring";
    public static final String TYPE_BYTE = "byte";

}
