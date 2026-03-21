package com.wmp.test;

import com.wmp.PublicTools.easteregg.EasterEgg;
import com.wmp.PublicTools.easteregg.EasterEggClear;

import javax.swing.*;

public class FunctionTest {
    public static void main(String[] args) throws Exception {

        EasterEgg.INSTANCE.UHelper(5);

        JDialog dialog = new JDialog();

        JButton button = new JButton("关闭");
        button.addActionListener(e -> {
            try {
                EasterEggClear.INSTANCE.UHelper(0);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(button);
        dialog.pack();
        dialog.setVisible(true);


    }
}
