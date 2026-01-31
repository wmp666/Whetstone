package com.wmp.whetstone.CTComponent.CTButton;

import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.appFileControl.IconControl;

import java.awt.*;

public class CTTextButton extends CTButton {


    public CTTextButton(String text) {
        this(text, null, IconControl.COLOR_DEFAULT, true);
    }

    public CTTextButton(String text, String name, int iconStyle) {
        this(text, name, iconStyle, true);
    }

    public CTTextButton(String text, boolean showBorder) {
        this(text, null, IconControl.COLOR_DEFAULT, showBorder);
    }

    public CTTextButton(String text, String name, int iconStyle, boolean showBorder) {

        if (name != null) {
            this.setIcon(name, iconStyle, 35, 35);
        }
        this.setText(text);
        this.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, CTFont.getSize(CTFontSizeStyle.NORMAL)));

    }
}

