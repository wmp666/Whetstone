package com.wmp.whetstone.CTComponent;

import javax.swing.*;

public class CTComboBox extends JComboBox<String> {
    public CTComboBox() {
        super();


    }

    public void addItems(String... items) {
        for (String item : items) {
            this.addItem(item);
        }
    }
}
