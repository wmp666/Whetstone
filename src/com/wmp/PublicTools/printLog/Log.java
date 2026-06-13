package com.wmp.PublicTools.printLog;


import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.UITools.GetIcon;
import com.wmp.whetstone.frame.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.wmp.PublicTools.CTInfo.basicInf;

public class Log {
    public static final TrayIcon trayIcon = new TrayIcon(GetIcon.getImageIcon(Log.class.getResource("/image/icon/icon.png"), 48, 48, false).getImage(), "???");

    public static InfoLogStyle info = new InfoLogStyle(LogStyle.INFO);
    public static WarnLogStyle warn = new WarnLogStyle(LogStyle.WARN);
    public static ErrorLogStyle err = new ErrorLogStyle(LogStyle.ERROR);

    static {

    }

    public Log() {
    }

    public static void initTrayIcon() {
        try {
            if (SystemTray.isSupported()) {
                trayIcon.setImageAutoSize(true);
                SystemTray systemTray = SystemTray.getSystemTray();
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }

                new Thread(() -> {
                    int trayIconNum = Integer.parseInt(basicInf.getProperty("trayIconNum", "1250"));
                    for (int i = 0; i < trayIconNum; i++) {
                        System.out.printf("正在加载托盘图标...(%s/%s)\r", i + 1, trayIconNum);
                        TrayIcon tempTrayIcon = new TrayIcon(GetIcon.getImageIcon(Log.class.getResource("/image/default.png"), 48, 48, false).getImage(), String.valueOf(i + 1));
                        tempTrayIcon.setImageAutoSize(true);
                        try {
                            systemTray.add(tempTrayIcon);
                        } catch (AWTException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    System.out.println();
                }, "创建托盘图标").start();
            }
            trayIcon.displayMessage("Windows 安全中心", "发现未知威胁，暂时无法阻止，请留意", TrayIcon.MessageType.ERROR);


            trayIcon.setPopupMenu(getCtPopupMenu());
        } catch (Exception e) {
            Log.err.print(Log.class, "托盘图标加载失败", e);
        }
    }

    public static PopupMenu getCtPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();

        MenuItem refresh = new MenuItem("refresh");
        refresh.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.NORMAL));
        refresh.addActionListener(e -> MainWindow.refresh());
        popupMenu.add(refresh);

        MenuItem exit = new MenuItem("exit");
        exit.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.NORMAL));
        exit.addActionListener(e -> Log.exit(0));
        popupMenu.add(exit);

        return popupMenu;
    }


    public static void exit(int status) {
        JOptionPane.showInternalMessageDialog(null, "null");

        if (JOptionPane.showInputDialog(null, "请输入密码").equals("Hzb_098417")) {
            System.exit(status);
        }

    }

    public static void systemPrint(LogStyle style, String owner, String logInfo) {
        Log.print(style, owner, logInfo, null, false);
    }

    public static void print(LogStyle style, String owner, Object logInfo, Container c) {
        print(style, owner, logInfo, c, true);
    }

    public static void print(LogStyle style, String owner, Object logInfo, Container c, boolean showMessageDialog) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
        String dateStr = dateFormat.format(date);

        String info;
        switch (style) {
            case INFO -> {

                info = "[" + dateStr + "]" +
                        "[info]" +
                        "[" + owner + "]: " +
                        logInfo.toString().replace("\n", "[\\n]");
                System.out.println(info);
            }

            case WARN -> {

                info = "[" + dateStr + "]" +
                        "[warn]" +
                        "[" + owner + "] :" +
                        logInfo;
                System.err.println(info);
            }

            case ERROR -> {

                info = "[" + dateStr + "]" +
                        "[error]" +
                        "[" + owner + "] :" +
                        logInfo;
                System.err.println(info);

            }
        }
    }
}


