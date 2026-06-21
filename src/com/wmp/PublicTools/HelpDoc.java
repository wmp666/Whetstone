package com.wmp.PublicTools;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.FuncHelpUnit;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import io.github.raghultech.markdown.swingfx.preview.MarkdownPanel;

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
        MarkdownPanel markdownPanel = new MarkdownPanel("""
            ## Jar
            1. 添加库：将`WhetStone`整个项目导入为库，添加开发时所需的其他库
            2. 创建彩蛋基本单元：在`com.wmp.whetstone`中新建类`EasterEggUnit`
            3. 实现方法：实现所有方法，并在
                - getTargetVersion()中返回开发时所用磨刀石版本
                - getID()中返回彩蛋ID
                - clear()中写入如何清理彩蛋,会传入需要清理的方法名
                - getVersion()返回彩蛋版本
                - help()中返回该彩蛋主要功能
                - funcHelps()中返回功能列表
            4. 编写彩蛋方法：可以使用任意名称，但是一定要在funcHelps()中,写明方法名和各参数作用
            参数：String[] args
            
            ## DLL
            1. 需要编写
                - const char* getID()：彩蛋ID
                - const char* getVersion()：版本号，a.b.c(不能超过三位),
                - const char* help()：编写DLL主要作用
                - const char* funcHelps()：各方法用法，编写规则 方法2名|功能介绍;方法2名|...;...
                - const char* clear()：清理彩蛋(可以不写，如果不需要清理彩蛋)
            2. 编写彩蛋方法：可以使用任意名称，但是一定要在funcHelps()中,写明方法名和各参数作用
                可输入的参数：int, char, wchar_t, long, double, (const) char*, (const) wchar_t*, enum
            """);
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.add(markdownPanel, "开发者帮助");
    }

    private static void initUserHelpUnit(JPanel helpInfCardPanel) {
        MarkdownPanel markdownPanel = new MarkdownPanel("""
                # 课程特色功能设置
                > 将于2.3.0正式实行——因为部分功能还在开发=)
                
                ## 步骤
                1. 导入：将**彩蛋**拖入`app/easter_egg`文件夹（**Jar**文件或**dll**文件）
                
                2. 链接：在`app/start_list.json`中设置启动方式
                
                3.  启动！
                
                # 配置文件设置
                1. 彩蛋单元：
                    ```json
                    {"id": "...", "funcName":"JAR中方法名", "func": "要使用功能", "args": ["value", ...]}
                    ```
                    - 如果调用的是dll：
                        ```json
                        {"id": "dll:...", "funcName":"DLL中方法名", "func": "要使用功能", "args": ["style:value", ...]}
                        ```
                
                    - 支持的功能：
                        > [varName] 表示要由用户输入的内容 如：for:4 循环4次
                
                      | 功能指令                | 功能                         |
                      |---------------------|----------------------------|
                      | while               | 死循环                        |
                      | for:[count]         | 循环[count]次                 |
                      | sleep:before:[time] | 在启动前休眠[time]毫秒             |
                      | sleep:after:[time]  | 在启动后休眠[time]毫秒             |
                      | sleep:while:[time]  | 在循环时的间隔休眠[time]毫秒（存在循环时可用） |
                
                    - 参数相关：
                       style:value 例子：long:1
                
                    - 值的设置：
                        1. 如果想要从大量数据中随机可以使用
                            ```
                            random[value1, value2, ...]
                            ```
                        2. 如果想要随机一个数字(整数)
                            ```
                            randomNum[min, max]  [min, max]
                            ```
                
                    - 支持的类型：
                      - byte, char, string, WString, int, double, long(更多内容需查看映射表)
                      ```json
                      {"id": "var:var_list.json中对应名"}
                      ```
                
                
                2. 变量列表：在`app/var_list.json`，用于化简**配置彩蛋启动参数**时所输入的彩蛋单元内容
                    ```json
                    {"变量名": {彩蛋单元}}, ...}
                    ```
                
                3. 彩蛋启动配置文件结构：
                    ```json
                    {"app_start":[...], "class_start": [...], "class_list": {"课程名" : [...], ...}}
                    ```
                
                4. 基础数据配置：在`app/settings.properties`中
                    ```propreties
                    trayIconNum=1250
                    serverPort=8697
                    recordingDirNum=5
                    ```
                    | 参数名             | 作用            |
                    |-----------------|---------------|
                    | trayIconNum     | 创建的系统托盘图标数    |
                    | serverPort      | 网络开放端口        |
                    | recordingDirNum | 录音文件夹（天数）数量上限 |
                
                # 其他:
                1. 可以通过同路径下的发信器(由Python开发)向磨刀石发送指令(端口号默认8697)
                
                    | 发送的指令                                 | 作用   |
                    |---------------------------------------|------|
                    | help                                  | 显示帮助 |
                    | run:EE:(彩蛋ID);(调用的方法名);(调用的功能);(彩蛋参数) | 运行彩蛋 |
                    | clear:EE:(彩蛋ID);(要清理的方法名)             | 清理彩蛋 |
                    | refresh                               | 刷新   |
                    | exit                                  | 关闭   |
                
                2. 启动参数：
                   - -help 打开帮助
                   - -admin:noneed 去除启动时的提权操作
                """);
        markdownPanel.setFont(UIManager.getFont("h2.font"));
        helpInfCardPanel.add(markdownPanel, "用户帮助");
    }

    private static void initEEHelpUnit(BasicEasterEggUnit[] units, JPanel helpInfCardPanel) {
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
            infoPanel.setMinimumSize(new Dimension());

            //显示彩蛋使用帮助
            JPanel helpPanel = new JPanel(new BorderLayout());
            //显示主要帮助内容
            JTextArea helpTextArea = new JTextArea(unit.help());
            helpTextArea.setEditable(false);
            helpTextArea.setFont(UIManager.getFont("h2.font"));
            helpPanel.add(new JScrollPane(helpTextArea), BorderLayout.NORTH);

            //各个方法的帮助
            FuncHelpUnit[] funcHelpUnits = unit.funcHelps();
            if (funcHelpUnits != null && funcHelpUnits.length > 0) {

                JTabbedPane funcHelpTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

                Arrays.stream(funcHelpUnits).forEach(funcHelp->{
                    //显示功能帮助
                    JTextArea funcHelpTextArea = new JTextArea(funcHelp.help());
                    funcHelpTextArea.setLineWrap(true);
                    funcHelpTextArea.setEditable(false);
                    funcHelpTextArea.setFont(UIManager.getFont("h2.font"));


                    funcHelpTabbedPane.addTab(funcHelp.funcName(), funcHelpTextArea);
                });

                helpPanel.add(funcHelpTabbedPane, BorderLayout.CENTER);
            }

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, helpPanel, infoPanel);


            splitPane.setDividerLocation(200);
            //splitPane.setOneTouchExpandable(true);
            helpInfPanel.add(splitPane, BorderLayout.CENTER);

            //添加卡片
            helpInfCardPanel.add(helpInfPanel, unit.getID());
        }
    }

}
