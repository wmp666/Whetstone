package com.wmp.PublicTools.appFileControl;


import javax.swing.*;

public class IconControl {
    public static final int COLOR_DEFAULT = 0;
    public static final int COLOR_COLORFUL = 1;


    public static ImageIcon getIcon() {
        return new ImageIcon(IconControl.class.getResource("/image/default.png"));
    }
}
