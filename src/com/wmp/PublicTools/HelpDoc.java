package com.wmp.PublicTools;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;


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

        //id + 其他帮助 的列表
        JList<String> idList = new JList<>();
        idList.setFont(UIManager.getFont("h1.font"));
        JScrollPane listScrollPane = new JScrollPane(idList);
        EEUnitHelpDialog.add(listScrollPane, BorderLayout.WEST);

        //创建帮助页底层的JPanel——用于换页
        JPanel helpInfCardPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        helpInfCardPanel.setLayout(cardLayout);

        //初始化列表
        LinkedList<String> list = new LinkedList<>(Arrays.stream(units).map(BasicEasterEggUnit::getID).toList());
        list.addFirst("开发者帮助");
        list.addFirst("用户帮助");
        idList.setListData(list.toArray(new String[0]));

        idList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedValue = idList.getSelectedValue();
                if (selectedValue != null) {
                    cardLayout.show(helpInfCardPanel, selectedValue);
                }
            }
        });

        //创建用户帮助页
        initUserHelpUnit(helpInfCardPanel);
        //创建开发者帮助页
        initDeveloperHelpUnit(helpInfCardPanel);

        //创建彩蛋帮助页
        initEEHelpUnit(units, helpInfCardPanel);

        EEUnitHelpDialog.add(helpInfCardPanel, BorderLayout.CENTER);


        EEUnitHelpDialog.setMinimumSize(new Dimension(800, 600));
        EEUnitHelpDialog.setVisible(true);
    }

    private static void initDeveloperHelpUnit(JPanel helpInfCardPanel) {
        JTextArea developerHelpTextArea = new JTextArea();
        developerHelpTextArea.setText("""
                1、添加库：将“WhetStone”整个项目导入为库，添加开发时所需的其他库
                2、创建彩蛋基本单元：在“com.wmp.whetstone”中新建类“EasterEggUnit”并继承“BasicEasterEggUnit”
                3、实现方法：实现所有抽象方法，并在getTargetVersion()中返回开发时所用磨刀石版本
                    getID()中返回彩蛋ID    run(String[] args)中写入当彩蛋如何启动   clean()中写入如何清理彩蛋    getVersion()返回彩蛋版本
                    在help()中返回该彩蛋主要功能
                    在funcHelp()中返回功能列表
                """);
        developerHelpTextArea.setFont(UIManager.getFont("h2.font"));
        developerHelpTextArea.setLineWrap(true);
        developerHelpTextArea.setEditable(false);
        helpInfCardPanel.add(new JScrollPane(developerHelpTextArea), "开发者帮助");
    }

    private static void initUserHelpUnit(JPanel helpInfCardPanel) {
        JTextArea userHelpTextArea = new JTextArea();
        userHelpTextArea.setText("""
                1. 导入：拖入“app/easter_egg”文件夹（Jar文件）
                2. 链接：在“app/start_list.json”中设置启动方式
                     ① 彩蛋单元：{"id": "...", "func": "要使用功能", "args": ["...", ...]}
                     如果调用的是dll：{"id": "dll:...", "funcName":"DLL中方法名", "func": "要使用功能", "args": ["style:value", ...]}
                     支持的功能：while->死循环 for:[count]->循环[count]次
                                sleep:before:[time]->在启动前休眠[time]毫秒
                                sleep:after:[time]->在启动后休眠[time]毫秒
                                sleep:while:[time]->在循环时的间隔休眠[time]毫秒（存在循环时可用）
                     支持的类型：byte, char, string, WString, int, double, long
                     可转换：{"id": "var:var_list.json中对应名"}
                
                     ② 变量列表：在“app/var_list.json”，用于化简启动方式设置时所输入的彩蛋单元内容
                     格式：{"变量名": {彩蛋单元}}, ...}
                
                     ③ 文件结构：{"app_start":[...], "class_start": [...], "class_list": {"课程名" : [...], ...}}
                3. 启动！
                
                其他:
                1.可以通过同路径下的发信器向磨刀石发送指令(端口号默认8697)
                    如:help run:EE:(彩蛋ID) clear:EE:(彩蛋ID)
                
                · -help 打开帮助
                """);
        userHelpTextArea.setFont(UIManager.getFont("h2.font"));
        userHelpTextArea.setLineWrap(true);
        userHelpTextArea.setEditable(false);
        helpInfCardPanel.add(new JScrollPane(userHelpTextArea), "用户帮助");
    }

    private static void initEEHelpUnit(BasicEasterEggUnit[] units, JPanel helpInfCardPanel) {
        for (BasicEasterEggUnit unit : units) {
            //创建帮助页——卡片
            JPanel helpInfPanel = new JPanel();
            helpInfPanel.setLayout(new BorderLayout());

            //显示ID
            JTextArea idTextArea = new JTextArea("彩蛋ID: " + unit.getID());
            idTextArea.setFont(UIManager.getFont("h0.font"));
            helpInfPanel.add(idTextArea, BorderLayout.NORTH);

            //显示其他信息
            JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            JLabel versionPanel = new JLabel("彩蛋版本: " + unit.getVersion());
            JLabel targetVersionPanel = new JLabel("彩蛋开发版本: " + unit.getTargetVersion());
            versionPanel.setFont(UIManager.getFont("h2.font"));
            targetVersionPanel.setFont(UIManager.getFont("h2.font"));
            infoPanel.add(versionPanel);
            infoPanel.add(targetVersionPanel);

            //兼容性警告
            if (!EasterEggControl.isCompatible(unit.getTargetVersion(), CTInfo.DEVELOP_VERSION)) {
                JLabel warningLabel = new JLabel("此彩蛋开发版本与当前版本不兼容");
                warningLabel.setForeground(Color.RED);
                warningLabel.setFont(UIManager.getFont("h2.font"));
                infoPanel.add(warningLabel);
            }

            helpInfPanel.add(new JScrollPane(infoPanel), BorderLayout.WEST);

            //显示彩蛋使用帮助
            JPanel helpPanel = new JPanel(new BorderLayout());
            //显示主要帮助内容
            JTextArea helpTextArea = new JTextArea(unit.help());
            helpTextArea.setEditable(false);
            helpTextArea.setFont(UIManager.getFont("h2.font"));
            helpPanel.add(new JScrollPane(helpTextArea), BorderLayout.NORTH);

            FuncHelpUnit[] funcHelpUnits = unit.funcHelps();
            if (funcHelpUnits != null && funcHelpUnits.length > 0) {


                //显示功能帮助
                JPanel funcHelpPanel = new JPanel(new BorderLayout());
                //显示功能列表
                JList<String> funcHelpList = new JList<>(
                        Arrays.stream(funcHelpUnits).map(FuncHelpUnit::funcName).toList().toArray(new String[0]));
                funcHelpList.setFont(UIManager.getFont("h2.font"));
                funcHelpList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                funcHelpPanel.add(new JScrollPane(funcHelpList), BorderLayout.WEST);
                //显示功能帮助
                JTextArea funcHelpTextArea = new JTextArea(funcHelpUnits[0].help());
                funcHelpTextArea.setLineWrap(true);
                funcHelpTextArea.setEditable(false);
                funcHelpTextArea.setFont(UIManager.getFont("h2.font"));
                funcHelpPanel.add(new JScrollPane(funcHelpTextArea), BorderLayout.CENTER);


                //初始化列表监听
                funcHelpList.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        int index = funcHelpList.getSelectedIndex();
                        if (index >= 0) {
                            funcHelpTextArea.setText(funcHelpUnits[index].help());
                        }
                    }
                });

                helpPanel.add(funcHelpPanel, BorderLayout.CENTER);
            }

            helpInfPanel.add(helpPanel, BorderLayout.CENTER);

            //添加卡片
            helpInfCardPanel.add(helpInfPanel, unit.getID());
        }
    }

}
