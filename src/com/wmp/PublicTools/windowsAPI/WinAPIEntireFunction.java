package com.wmp.PublicTools.windowsAPI;

import com.sun.jna.platform.win32.WinDef;

public class WinAPIEntireFunction {

    public static final byte VK_LWIN = (byte) 0x5B;

    public static void pressKey(byte keyCode) {
        //模拟按下
        User32.INSTANCE.keybd_event(keyCode, (byte) 0, 0x0000, 0);

        // 短暂延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //模拟松开
        User32.INSTANCE.keybd_event(keyCode, (byte) 0, 0x0002, 0);
    }

    // 光栅操作码
    public static final int DSTINVERT = 0x00550009;  // 反转目标矩形

    public static void invertScreenWithJNA() throws InterruptedException {
        // 获取桌面窗口句柄
        WinDef.HWND desktop = com.sun.jna.platform.win32.User32.INSTANCE.GetDesktopWindow();

        // 获取屏幕设备上下文
        WinDef.HDC hdc = com.sun.jna.platform.win32.User32.INSTANCE.GetDC(desktop);

        // 获取屏幕尺寸
        int width = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(
                com.sun.jna.platform.win32.User32.SM_CXSCREEN);
        int height = com.sun.jna.platform.win32.User32.INSTANCE.GetSystemMetrics(
                com.sun.jna.platform.win32.User32.SM_CYSCREEN);

        // 使用PatBlt反转整个屏幕
        GDI32.INSTANCE.PatBlt(hdc, 0, 0, width, height, DSTINVERT);

        Thread.sleep(3000);
    }

    public static void clearInvertScreen(){
        // 获取桌面窗口句柄
        WinDef.HWND desktop = com.sun.jna.platform.win32.User32.INSTANCE.GetDesktopWindow();

        // 获取屏幕设备上下文
        WinDef.HDC hdc = com.sun.jna.platform.win32.User32.INSTANCE.GetDC(desktop);


        // 清理资源
        com.sun.jna.platform.win32.User32.INSTANCE.ReleaseDC(desktop, hdc);
    }
}
