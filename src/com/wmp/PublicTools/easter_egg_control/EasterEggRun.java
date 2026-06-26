package com.wmp.PublicTools.easter_egg_control;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import java.awt.*;
import java.util.ArrayList;

public class EasterEggRun {
    public static void run(String EEKey, String threadNameHead) {


        try {
            CTInfo.EEMap.getOrDefault(EEKey, new ArrayList<>())
                    .forEach(easterEggUnit -> {
                        Thread.ofVirtual()
                                .name(threadNameHead + "_" + easterEggUnit.easterEggUnit().getID())
                                .start(() -> {
                                    try {
                                        Log.info.print(ClassFormPanel.class.toString(),
                                                String.format("启动彩蛋：%s|版本：%s|开发库版本：%s", easterEggUnit.easterEggUnit().getID(), easterEggUnit.easterEggUnit().getVersion(), easterEggUnit.easterEggUnit().getTargetVersion()));
                                        easterEggUnit.easterEggUnit().run(easterEggUnit.args());
                                    } catch (Exception _) {
                                        Log.trayIcon.displayMessage("噢,天呐!", "搞砸了呢...", TrayIcon.MessageType.ERROR);
                                    }
                                });
                    });
        } catch (Exception e) {
            Log.trayIcon.displayMessage("噢,天呐!", "搞砸了呢...", TrayIcon.MessageType.ERROR);
        }


    }

    public static void clear(String key) {
        //获取到对应彩蛋单元
        try {
            CTInfo.EEMap.getOrDefault(key, new ArrayList<>()).forEach(easterEggUnit -> {
                Thread.ofVirtual()
                        .name("Thread:clear EasterEgg " + easterEggUnit.easterEggUnit().getID())
                        .start(()->{
                    try {
                        easterEggUnit.easterEggUnit().clear(easterEggUnit.args()[0]);
                    } catch (Exception e) {
                        Log.err.print(EasterEggRun.class, String.format("清除彩蛋失败：%s", easterEggUnit.easterEggUnit().getID()));
                    }
                });
            });
        } catch (Exception e) {
            Log.err.print(EasterEggRun.class, String.format("清除彩蛋组失败：%s", key));
        }
    }
}
