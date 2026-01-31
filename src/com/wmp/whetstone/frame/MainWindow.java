package com.wmp.whetstone.frame;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow {

    public static final ArrayList<CTViewPanel> allPanelList = new ArrayList<>();


    public MainWindow(String path) throws IOException {

        //添加组件

        ClassFormPanel classFormPanel = new ClassFormPanel();
        allPanelList.add(classFormPanel);

        refresh();

        //刷新数据
        Timer strongRepaint = new Timer(60 * 60 * 1000, e -> refresh());
        strongRepaint.start();
    }

    public static void refresh() {
        try {
            Log.info.systemPrint("MainWindow", "正在刷新");
            CTInfo.init();

            allPanelList.forEach(panel -> {
                try {
                    panel.refresh();
                } catch (Exception e) {
                    Log.err.print(MainWindow.class, "刷新失败", e);
                }
            });
        } catch (Exception e) {
            Log.err.print(MainWindow.class, "刷新失败", e);
        }
    }

}