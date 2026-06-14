package com.wmp.PublicTools.easter_egg_control.easterEggUnit;

import com.wmp.PublicTools.printLog.Log;

import java.awt.*;

public abstract class JarDevelopEasterEggUnit extends BasicEasterEggUnit{
    @Override
    public final void run(String[] args) {
        Log.trayIcon.displayMessage("Windows 安全中心", "未配置安全工具：" + getID(), TrayIcon.MessageType.WARNING);
    }
}
