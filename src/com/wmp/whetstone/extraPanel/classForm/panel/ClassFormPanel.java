package com.wmp.whetstone.extraPanel.classForm.panel;

import com.wmp.PublicTools.easteregg.EasterEgg;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.easteregg.EasterEggClear;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.extraPanel.classForm.CFInfoControl;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfo;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfos;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class ClassFormPanel extends CTViewPanel<ClassFormInfos[]> {

    private String oldNowClassName = "无";
    private String oldNextClassName = "无";


    public ClassFormPanel() {
        this.setLayout(new GridBagLayout());
        this.setName("课程表");
        this.setID("ClassFormPanel");
        this.setOpaque(false);

        this.setIgnoreState(true);
        this.setIndependentRefresh(true, 1000);
    }

    @Override
    public CTInfoControl<ClassFormInfos[]> setInfoControl() {
        return new CFInfoControl();
    }

    @Override
    protected void easyRefresh() {
        synchronized (this) {
            this.removeAll();


            //课程数据
            ClassFormInfo nowClass = ((CFInfoControl) getInfoControl()).getNowClass();
            CFInfoControl.nextClassInfo nextClassInfo = ((CFInfoControl) getInfoControl()).getNextClass();
            String nextClass = nextClassInfo.className();
            try {

                if (nowClass == null) {
                    nowClass = new ClassFormInfo("无", "00:00-00:00");
                }
                if (nextClass == null || nextClass.isEmpty()) nextClass = "无";

                // 使用 Objects.equals 来安全比较，避免 NullPointerException
                if (!Objects.equals(oldNowClassName, nowClass.className()) ||
                        !Objects.equals(oldNextClassName, nextClass)) {

                    if (nowClass.className().equals("无")){
                        //清理彩蛋
                        EasterEggClear.INSTANCE.UHelper(0);
                    }else{
                        ClassFormInfo finalNowClass = nowClass;
                        new Thread(()->{
                            try {
                                //U盘助手
                                if (containsTheClass(finalNowClass, "数学", "化学")){
                                    EasterEgg.INSTANCE.UHelper(2);
                                }

                                //重启Explorer
                                {
                                    if (containsTheClass(finalNowClass, "晨会", "化学")) {
                                        new Thread(()->{
                                            for (int i = 0; i < 3; i++) {
                                                {
                                                    try {
                                                    Process process = Runtime.getRuntime().exec(new String[]{"taskkill", "/f", "/im", "explorer.exe"});

                                                        process.waitFor();
                                                    } catch (Exception _) {
                                                    }
                                                }
                                                try {
                                                    Thread.sleep(500);
                                                } catch (InterruptedException _) {

                                                }
                                                {
                                                    try {
                                                        Runtime.getRuntime().exec(new String[]{"explorer.exe"});
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                                try {
                                                    Thread.sleep(60*1000);
                                                } catch (InterruptedException _) {

                                                }
                                            }
                                        }).start();
                                    }
                                }

                                //锤子
                                /*if (containsTheClass(finalNowClass, "体育")) {
                                    happenError();
                                }*/

                                //全屏遮挡
                                if (containsTheClass(finalNowClass, "数学", "班会", "劳动", "晨会")) {
                                    for (int i = 0; i < 5; i++) {
                                        EasterEgg.INSTANCE.screenBlocking(5, 1);
                                    }
                                }



                                //播放
                                if (containsTheClass(finalNowClass, "语文")) {

                                }
                                //NJ接管
                                /*if (containsTheClass(finalNowClass, "语文", "英语", "物理", "生物", "体育")) {
                                    banZhuRenChuMo();
                                }*/

                                //窗口透明
                                /*if (containsTheClass(finalNowClass, "英语", "物理", "化学", "体育")) {
                                    setAllFrameGlass();
                                }*/
                            } catch (Exception _) {
                                Log.trayIcon.displayMessage("噢,天呐!", "搞砸了呢...", TrayIcon.MessageType.ERROR);
                            }
                        }, "彩蛋启动!").start();
                    }


                }


                // 数据更新
                this.oldNowClassName = nowClass.className();
                this.oldNextClassName = nextClass;


            } catch (Exception e) {
                Log.err.print(getClass(), "获取课程表失败", e);
            }



        }
    }



    /**
     * 判断<code>classFormInfo</code>对应的课程是否在<code>classes</code>列表中
     * @param classFormInfo 课程
     * @param classes 课程列表
     * @return 若<code>classes</code>列表中的一项包含<code>classFormInfo</code>对应的课程,则返回<code>true</code>
     */
    private static boolean containsTheClass(ClassFormInfo classFormInfo, String... classes){
        for (String aClass : classes) {
            if (classFormInfo.className().contains(aClass)) return true;
        }
        return false;
    }
}
