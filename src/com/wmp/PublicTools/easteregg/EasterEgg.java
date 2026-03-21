package com.wmp.PublicTools.easteregg;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.CTColor;
import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.PublicTools.io.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.PublicTools.windowsAPI.BlurGlassEffect;
import com.wmp.PublicTools.windowsAPI.DesktopAppEnumerator;
import com.wmp.PublicTools.windowsAPI.DisableGlassEffect;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;
import com.wmp.whetstone.extraPanel.classForm.panel.ClassFormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class EasterEgg extends BasicEasterEggFunction{

    public static final EasterEgg INSTANCE = new EasterEgg();

    /**
     * 屏幕遮挡
     * @param maxWaitTime 启动前的等待时间(min)
     * @param showTime 遮挡时间(s)
     * @throws Exception
     */
    public void screenBlocking(int maxWaitTime, int showTime) throws Exception {
        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();


        int waitTime = (new Random().nextInt(maxWaitTime) + 1);
        Log.info.print(ClassFormPanel.class.toString(), String.format("预启动：屏幕遮挡|参数：启动前等待时间:%smin;显示时间:%ss;大小：%s", waitTime, showTime, d));

        Thread.sleep((long) waitTime * 60 * 1000);

        //创建一个robot对象
        Robot robot = new Robot();

        //创建该分辨率的矩形对象
        Rectangle screenRect = new Rectangle(d);
        //根据这个矩形截图
        BufferedImage bufferedImage = robot.createScreenCapture(screenRect);

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
                    Thread.sleep(showTime * 1000);
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

        dialog.setVisible(true);
        Log.info.print(ClassFormPanel.class.toString(), "屏幕遮挡完毕！");
    }

    public void happenError() throws Exception {

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

    /**
     * “U盘助手”——用于弹出安全/不安全的U盘
     * @param count 个数
     */
    public void UHelper(int count){
        ResourceLocalizer.copyEmbeddedFile(CTInfo.TEMP_PATH + "\\Whetstone\\", "/resource/", "Uhelper.exe");


        //Desktop.getDesktop().open(new java.io.File(CTInfo.TEMP_PATH + "\\Whetstone\\chuizis.exe"));
        new Thread(()->{

                for(int i = 0;i < count;i++){
                    try {
                        Process process = Runtime.getRuntime().exec(new String[]{CTInfo.TEMP_PATH + "\\Whetstone\\Uhelper.exe"});
                        int status = process.waitFor();

                        Log.info.print(ClassFormPanel.class.toString(), "UHelper.exe关闭：" + status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        }, "U盘助手").start();

    }

    public void setAllFrameGlass() throws Exception {

        Thread.sleep((new Random().nextInt(3) + 1)*60*1000);

        for (int i = 0; i < 50; i++) {
            {
                java.util.List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
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

    public void banZhuRenChuMo() {
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

    public void reStartExplorer() {
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
