package com.wmp.PublicTools.easteregg;


public abstract class BasicEasterEggFunction {

    /**
     * 屏幕遮挡
     * @param maxWaitTime 启动前的等待时间(min)
     * @param showTime 遮挡时间(s)
     */
    public abstract void screenBlocking(int maxWaitTime, int showTime) throws Exception;

    public abstract void happenError() throws Exception;

    /**
     * “U盘助手”——用于弹出安全/不安全的U盘
     * @param count 个数
     */
    public abstract void UHelper(int count);

    public abstract void setAllFrameGlass() throws Exception;

    public abstract void banZhuRenChuMo();

    public abstract void reStartExplorer();

    public abstract void videoPlayer();

}
