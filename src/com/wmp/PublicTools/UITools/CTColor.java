package com.wmp.PublicTools.UITools;

import com.wmp.PublicTools.printLog.Log;

import java.awt.*;

public class CTColor {
    /**
     * 主色 白色
     */
    public static final String MAIN_COLOR_WHITE = "white";
    /**
     * 主色 蓝色
     */
    public static final String MAIN_COLOR_BLUE = "blue";
    /**
     * 主色 绿色
     */
    public static final String MAIN_COLOR_GREEN = "green";
    /**
     * 主色 红色
     */
    public static final String MAIN_COLOR_RED = "red";
    /**
     * 主色 黑色
     */
    public static final String MAIN_COLOR_BLACK = "black";

    /**
     * 主题 灰色
     */
    public static final String STYLE_DARK = "dark";
    /**
     * 主题 亮色
     */
    public static final String STYLE_LIGHT = "light";

    public static final String STYLE_SYSTEM = "system";

    public static String style = STYLE_LIGHT;
    public static Color mainColor = new Color(0x29A8E3);
    public static Color textColor = getParticularColor(MAIN_COLOR_BLACK);
    public static Color backColor = getParticularColor(MAIN_COLOR_WHITE);

    public static void setColorList(Color mainColor, Color backColor, Color textColor, String theme) {
        CTColor.mainColor = mainColor;
        CTColor.backColor = backColor;
        CTColor.textColor = textColor;
        style = theme;
    }

    public static void setAllColor(String mainColorStr, String tempStyle) {

        setMainColor(mainColorStr);
        setMainTheme(tempStyle);

    }

    private static void setMainColor(String mainColorStr) {

        mainColor = getParticularColor(mainColorStr);

        Log.info.print("CTColor", "mainColor:" + String.format("#%06x", mainColor.getRGB()));
    }

    private static void setMainTheme(String tempStyle) {


        return;
    }

    /**
     * 获取指定颜色
     *
     * @param colorStyle 颜色样式
     * @return 颜色
     * @see CTColor
     * @see #MAIN_COLOR_WHITE
     * @see #MAIN_COLOR_BLUE
     * @see #MAIN_COLOR_GREEN
     * @see #MAIN_COLOR_RED
     * @see #MAIN_COLOR_BLACK
     * @see #STYLE_DARK
     * @see #STYLE_LIGHT
     *
     */
    public static Color getParticularColor(String colorStyle) {
        return switch (colorStyle) {
            case MAIN_COLOR_WHITE -> new Color(0xF0F0F0);
            case MAIN_COLOR_BLUE -> new Color(0x29A8E3);
            case MAIN_COLOR_GREEN -> new Color(0x05E666);
            case MAIN_COLOR_RED -> new Color(0xFF0000);
            case MAIN_COLOR_BLACK -> new Color(0x282C34);
            case STYLE_DARK -> getParticularColor(MAIN_COLOR_BLACK);
            case STYLE_LIGHT -> getParticularColor(MAIN_COLOR_WHITE);
            default -> new Color(0x29A8E3);
        };

    }

    /**
     * 获取主题颜色
     *
     * @param theme 主题
     * @return 主题颜色 0:背景色 1:文字色
     */
    public static Color[] getThemeColor(String theme) {
        return switch (theme) {
            case STYLE_DARK -> new Color[]{
                    getParticularColor(MAIN_COLOR_BLACK),
                    getParticularColor(MAIN_COLOR_WHITE)
            };
            default -> new Color[]{
                    getParticularColor(MAIN_COLOR_WHITE),
                    getParticularColor(MAIN_COLOR_BLACK)
            };
        };
    }

    @Override
    public String toString() {
        return "CTColor{ mainColor:" + String.format("#%06x", mainColor.getRGB()) +
                " textColor:" + String.format("#%06x", textColor.getRGB()) +
                " backColor:" + String.format("#%06x", backColor.getRGB()) + "}";
    }
}
