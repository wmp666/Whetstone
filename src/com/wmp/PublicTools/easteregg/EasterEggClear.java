package com.wmp.PublicTools.easteregg;

import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;

/**
 * 用于清理没有运行完的彩蛋
 */
public class EasterEggClear extends BasicEasterEggFunction{
    public static final EasterEggClear INSTANCE = new EasterEggClear();
    @Override
    public void screenBlocking(int maxWaitTime, int showTime) throws Exception {

    }

    @Override
    public void happenError() throws Exception {

    }

    /**
     * 停止U盘助手
     * @param count 无效参数
     */
    @Override
    public void UHelper(int count) throws Exception {
        //关闭U盘助手线程
        Thread.getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().contains("U盘助手"))
                .findFirst()
                .orElse(new Thread()).interrupt();

        //关闭现有的U盘助手
        WinAPIEntireFunction.killProcess("UHelper.exe", 100);
    }

    @Override
    public void setAllFrameGlass() throws Exception {

    }

    @Override
    public void banZhuRenChuMo() throws Exception {

    }
}
