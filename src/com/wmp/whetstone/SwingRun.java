package com.wmp.whetstone;

import com.wmp.PublicTools.CTInfo;
import com.wmp.whetstone.frame.MainWindow;

import java.io.IOException;

public class SwingRun {

    //, TreeMap<String, StartupParameters> allArgs, ArrayList<String> list
    public static void show() throws IOException {

        new MainWindow(CTInfo.DATA_PATH);
    }
}
