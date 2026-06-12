package com.wmp.PublicTools.easter_egg_control;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import java.awt.*;
import java.util.ArrayList;

public class EasterEggRun {
    public static void run(String EEKey, String threadNameHead) {


        CTInfo.EEMap.getOrDefault(EEKey, new ArrayList<>())
                .forEach(easterEggUnit -> {
                    new Thread(() -> {
                        try {
                            Log.info.print(ClassFormPanel.class.toString(),
                                    String.format("启动彩蛋：%s|版本：%s|开发库版本：%s", easterEggUnit.easterEggUnit().getID(), easterEggUnit.easterEggUnit().getVersion(), easterEggUnit.easterEggUnit().getTargetVersion()));
                            easterEggUnit.easterEggUnit().run(easterEggUnit.args());
                        } catch (Exception _) {
                            Log.trayIcon.displayMessage("噢,天呐!", "搞砸了呢...", TrayIcon.MessageType.ERROR);
                        }
                    }, threadNameHead + "_" + easterEggUnit.easterEggUnit().getID()).start();
                });


    }

    public static void clear(String key){
        CTInfo.EEMap.getOrDefault(key, new ArrayList<>()).forEach(easterEggUnit -> {
            try {
                easterEggUnit.easterEggUnit().clear();
            } catch (Exception e) {
                Log.err.print(EasterEggRun.class, String.format("清除彩蛋失败：%s",easterEggUnit.easterEggUnit().getID()));
            }
        });
    }
}
