package com.wmp.PublicTools;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.FuncArgUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.PublicTools.io.IOForInfo;
import com.wmp.PublicTools.printLog.Log;
import io.github.raghultech.markdown.swingfx.preview.MarkdownPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class HelpDoc {
    public static void help() {
        createHelpDialog(EasterEggControl.installAll(true).toArray(new BasicEasterEggUnit[0]));
    }

    private static void createHelpDialog(BasicEasterEggUnit[] units) {
        JDialog EEUnitHelpDialog = new JDialog((Frame) null, "帮助", true);
        EEUnitHelpDialog.setTitle("帮助");
        EEUnitHelpDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        EEUnitHelpDialog.setLayout(new BorderLayout());
        EEUnitHelpDialog.setAlwaysOnTop(true);

        //警告
        JLabel warning = new JLabel("由于MarkDown文本加载需要初始化，可能需要等待较长时间或重新打开");
        warning.setForeground(Color.RED);
        EEUnitHelpDialog.add(warning, BorderLayout.SOUTH);

        //id + 其他帮助 的列表
        JTabbedPane idTabbedPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
        idTabbedPane.setFont(UIManager.getFont("h1.font"));

        //初始化列表


        //创建用户帮助页
        initUserHelpUnit(idTabbedPane);
        //创建开发者帮助页
        initDeveloperHelpUnit(idTabbedPane);
        //创建录音功能用法帮助页
        initRecordingHelpUnit(idTabbedPane);
        //创建映射表帮助页
        initDevelopTypeMappingTableHelpUnit(idTabbedPane);

        initExampleHelpUnit(idTabbedPane);
        //创建彩蛋帮助页
        initEEHelpUnit(units, idTabbedPane);


        EEUnitHelpDialog.add(idTabbedPane, BorderLayout.CENTER);


        EEUnitHelpDialog.setMinimumSize(new Dimension(800, 600));
        EEUnitHelpDialog.pack();
        EEUnitHelpDialog.setLocationRelativeTo(null);
        EEUnitHelpDialog.setVisible(true);
    }

    private static void initDeveloperHelpUnit(JTabbedPane helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel(
                IOForInfo.getInfos(HelpDoc.class.getResource("/helpDoc/开发者帮助.md")));
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.addTab("开发者帮助", markdownPanel);
    }

    private static void initUserHelpUnit(JTabbedPane helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel(
                IOForInfo.getInfos(HelpDoc.class.getResource("/helpDoc/用户帮助.md"))
        );
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.addTab("用户帮助", markdownPanel);
    }

    private static void initRecordingHelpUnit(JTabbedPane helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel(
                IOForInfo.getInfos(HelpDoc.class.getResource("/helpDoc/录音功能.md"))
        );
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.addTab("录音功能", markdownPanel);
    }

    private static void initDevelopTypeMappingTableHelpUnit(JTabbedPane helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel(
                IOForInfo.getInfos(HelpDoc.class.getResource("/helpDoc/DLL彩蛋参数映射表.md"))
        );
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.addTab("DLL彩蛋参数映射表", markdownPanel);
    }

    private static void initExampleHelpUnit(JTabbedPane helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel(
                IOForInfo.getInfos(HelpDoc.class.getResource("/helpDoc/example.md"))
        );
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.addTab("实例", markdownPanel);
    }

    private static void initEEHelpUnit(BasicEasterEggUnit[] units, JTabbedPane helpInfCardPanel) {
        for (BasicEasterEggUnit unit : units) {
            //创建帮助页——卡片
            JPanel helpInfPanel = new JPanel();
            helpInfPanel.setLayout(new BorderLayout());

            //显示ID
            JTextArea idTextArea = new JTextArea(unit.getID());
            idTextArea.setFont(UIManager.getFont("h0.font"));
            helpInfPanel.add(idTextArea, BorderLayout.NORTH);

            //显示其他信息

            //使用Markdown加载信息
            StringBuilder sb = new StringBuilder();
            sb.append("### 彩蛋ID\n**").append(unit.getID()).append("**\n")
                    .append("### 彩蛋版本\n**").append(unit.getVersion()).append("**\n");
            //兼容性警告
            if (!EasterEggControl.isCompatible(unit.getTargetVersion(), CTInfo.DEVELOP_VERSION)) {
                sb.append("### ~~彩蛋开发版本\n**").append(unit.getTargetVersion()).append("**\n");
            }
            sb.append("### 是否支持同时启动多个彩蛋\n**").append(unit.isSupportsMultipleEE()).append("**\n");

            MarkdownPanel infoPanel = new MarkdownPanel(sb.toString());

            //显示彩蛋使用帮助
            JPanel helpPanel = new JPanel(new BorderLayout());
            //显示主要帮助内容
            JTextArea helpTextArea = new JTextArea(unit.help() + "\n");
            helpTextArea.setEditable(false);
            helpTextArea.setFont(UIManager.getFont("h2.font"));
            helpPanel.add(new JScrollPane(helpTextArea), BorderLayout.NORTH);

            //各个方法的帮助
            var funcHelpUnits = unit.funcHelps();

            //确认存在funcHelps
            if (funcHelpUnits != null && funcHelpUnits.length > 0) {

                JTabbedPane funcHelpTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);



                Arrays.stream(funcHelpUnits).forEach(funcHelp->{//方法帮助

                    //MarkDown格式帮助
                    StringBuilder FuncArgsSB = new StringBuilder();
                    FuncArgsSB.append("# 方法帮助").append("\n")
                            .append(funcHelp.help()).append("\n");

                    try {
                        //方法的参数单元Map 方法名->参数单元[]
                        var funcArgsUnits = new HashMap<String, FuncArgUnit[]>(){
                            @Override
                            public String toString() {
                                Iterator<Entry<String, FuncArgUnit[]>> i = entrySet().iterator();
                                if (! i.hasNext())
                                    return "{}";

                                StringBuilder sb = new StringBuilder();
                                sb.append('{');
                                for (;;) {
                                    Entry<String, FuncArgUnit[]> e = i.next();
                                    String key = e.getKey();
                                    FuncArgUnit[] value = e.getValue();
                                    sb.append(key);
                                    sb.append('=');
                                    sb.append(Arrays.toString(value));
                                    if (! i.hasNext())
                                        return sb.append('}').toString();
                                    sb.append(',').append(' ');
                                }
                            }
                        };
                        //获取方法参数单元
                        var funcArgsUnitsList = unit.funcArgs();
                        if (funcArgsUnitsList != null && funcArgsUnitsList.length > 0) {
                            //将方法参数单元转换成Map
                            Arrays.stream(funcArgsUnitsList).forEach(funcArgsUnit ->
                                    funcArgsUnits.put(funcArgsUnit.funcName(), funcArgsUnit.args()));
                            Log.info.print("HelpDoc", "获取参数信息成功\n" + funcArgsUnits);
                            FuncArgsSB.append("# 参数").append("\n");
                            //获取方法中的参数信息——[]
                            var array = funcArgsUnits.get(funcHelp.funcName());
                            if (array != null) {
                                Arrays.stream(array).forEach(funcArgUnit -> {
                                    FuncArgsSB.append("## ").append(funcArgUnit.name()).append(" : ").append(funcArgUnit.type())
                                            .append(" (默认值：").append(funcArgUnit.isHasDefaultValue()?funcArgUnit.defaultValue():"无").append(")\n")
                                            .append(funcArgUnit.help()).append("\n");
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.err.print(HelpDoc.class, "获取参数信息失败", e);
                    }

                    //显示功能帮助
                    MarkdownPanel funcHelpMarkdown = new MarkdownPanel(FuncArgsSB.toString());
                    funcHelpMarkdown.setFont(UIManager.getFont("h3.font"));


                    funcHelpTabbedPane.addTab(funcHelp.funcName(), funcHelpMarkdown);
                });

                helpPanel.add(funcHelpTabbedPane, BorderLayout.CENTER);
            }

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, helpPanel, infoPanel);


            splitPane.setDividerLocation(350);
            //splitPane.setOneTouchExpandable(true);
            helpInfPanel.add(splitPane, BorderLayout.CENTER);

            //添加卡片
            helpInfCardPanel.addTab( unit.getID(), helpInfPanel);
        }
    }

}
