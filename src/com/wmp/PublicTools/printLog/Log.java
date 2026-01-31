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

public class Log {
    public static final TrayIcon trayIcon = new TrayIcon(GetIcon.getImageIcon(Log.class.getResource("/image/icon/icon.png"), 48, 48, false).getImage(), "ClassTools");

    public static InfoLogStyle info = new InfoLogStyle(LogStyle.INFO);
    public static WarnLogStyle warn = new WarnLogStyle(LogStyle.WARN);
    public static ErrorLogStyle err = new ErrorLogStyle(LogStyle.ERROR);

    static {
        if (SystemTray.isSupported()) {
            trayIcon.setImageAutoSize(true);
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }

        trayIcon.displayMessage("磨刀石", "磨刀石正在打磨你的电脑,会有奇效", TrayIcon.MessageType.INFO);

    }
    public Log() {
    }

    public static void initTrayIcon() {
        trayIcon.setPopupMenu(getCtPopupMenu());
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

        if (JOptionPane.showInputDialog(null, "请输入密码").equals("Hzb_098417")){
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


