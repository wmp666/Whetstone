package com.wmp.test;


import com.wmp.PublicTools.windowsAPI.User32;

public class WindowsAPITest {
    public static void main(String[] args) {

        //模拟按下
        User32.INSTANCE.keybd_event((byte) 0x5B, (byte) 0, 0x0000, 0);

        // 短暂延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //模拟松开
        User32.INSTANCE.keybd_event((byte) 0x5B, (byte) 0, 0x0002, 0);
    }
}
