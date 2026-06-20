package com.wmp.PublicTools.easter_egg_control.var;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Var(String basicValue, String style) {
    /**
     * 将传入的字符串转换成DLLVar
     *
     * @param str style:value
     * @return DLLVar
     */
    public static Var StringToVar(String str) {
        String[] split = str.split(":", 2);
        if (split.length == 2)
            return new Var(split[1], split[0]);
        else return new Var(split[0], "string");
    }

    private static String initValue(String basicValue){
        if (basicValue.startsWith("randomNum")) {
            String str = basicValue.substring(9);
            // 1. 正则取出括号内的内容（去掉了两边的 [ 和 ]）
            Pattern p = Pattern.compile("\\[(.*?)]");
            Matcher m = p.matcher(str);
            if (m.find()) {
                String inner = m.group(1);

                // 2. 按逗号分割（trim去掉可能的空格）
                String[] items = inner.split(",\\s*");
                long min = Long.parseLong(items[0]);
                long max = Long.parseLong(items[1]);

                return Long.toString(new Random().nextLong(max - min + 1) + min);
            }
        } else if (basicValue.startsWith("random")) {
            String randomListStr = basicValue.substring(6);

            // 1. 正则取出括号内的内容（去掉了两边的 [ 和 ]）
            Pattern p = Pattern.compile("\\[(.*?)]");
            Matcher m = p.matcher(randomListStr);
            if (m.find()) {
                String inner = m.group(1); // 得到 "数据1, 数据2, 数据3"

                // 2. 按逗号分割（trim去掉可能的空格）
                String[] items = inner.split(",\\s*");
                // items[0] = "数据1", items[1] = "数据2", items[2] = "数据3"

                return items[new Random().nextInt(items.length)];
            }
        }
        return basicValue;
    }

    public Object toTargetStyle() {
        var value = initValue(basicValue);
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
