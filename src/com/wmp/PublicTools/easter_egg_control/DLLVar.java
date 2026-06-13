package com.wmp.PublicTools.easter_egg_control;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;

public record DLLVar(String value, String style) {
    /**
     * 将传入的字符串转换成DLLVar
     *
     * @param str style:value
     * @return DLLVar
     */
    public static DLLVar StringToVar(String str) {
        String[] split = str.split(":", 2);
        return new DLLVar(split[1], split[0]);
    }

    public Object toTargetStyle() {
        return switch (style) {
            case "byte" -> Byte.parseByte(value);
            case "char" -> value.charAt(0);
            case "WString" -> new WString(value);
            case "int" -> Integer.parseInt(value);
            case "double" -> Double.parseDouble(value);
            case "long" -> new NativeLong(Long.parseLong(value));
            default -> value;
        };
    }
}
