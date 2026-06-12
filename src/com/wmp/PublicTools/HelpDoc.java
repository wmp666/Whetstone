package com.wmp.PublicTools;

import com.wmp.PublicTools.easter_egg_control.EasterEggControl;
import com.wmp.PublicTools.easter_egg_control.easterEggUnit.BasicEasterEggUnit;
import com.wmp.whetstone.CTComponent.CTOptionPane;

import java.util.List;


public class HelpDoc {
    public static void userHelp(){
        CTOptionPane.showMessageDialog(null, "帮助", """
                    1. 导入：拖入“app/easter_egg”文件夹（Jar文件）
                    2. 链接：在“app/start_list.json”中设置启动方式
                         ① 彩蛋单元：{"id": "...", "args": ["...", ...]}
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
                    
                    · -help:user 显示此类
                    · -help:develop 显示开发者帮助
                    · -help:EEID:all 显示所有彩蛋的使用帮助
                    · -help:EEID 彩蛋ID 显示指定彩蛋的使用帮助
                    """, null, CTOptionPane.INFORMATION_MESSAGE, true);
    }

    public static void developerHelp(){
        CTOptionPane.showMessageDialog(null, "帮助", """
            1、添加库：将“WhetStone”整个项目导入为库，添加开发时所需的其他库
            2、创建彩蛋基本单元：在“com.wmp.whetstone”中新建类“EasterEggUnit”并继承“BasicEasterEggUnit”
            3、实现方法：实现所有抽象方法，并在getTargetVersion()中返回开发时所用磨刀石版本
                getID()中返回彩蛋ID    run(String[] args)中写入当彩蛋如何启动   clean()中写入如何清理彩蛋    getVersion()返回彩蛋版本
                在help()中返回该彩蛋运行所需参数、功能...
                help()在显示时，拼接  彩蛋ID:.... \\n 彩蛋版本..: \\n 彩蛋开发版本:... \\n 彩蛋使用帮助:... \\n
            """, null, CTOptionPane.INFORMATION_MESSAGE, true);

    }

    public static void EEHelp(String EEID){
        List<BasicEasterEggUnit> basicEasterEggUnits = EasterEggControl.installAll(true);
        String helpText = buildEasterEggHelpText(basicEasterEggUnits, "彩蛋使用帮助", EEID);
        CTOptionPane.showMessageDialog(null, "彩蛋使用帮助", helpText, null ,CTOptionPane.INFORMATION_MESSAGE , true);

    }

    public static void AllEEHelp(){
        List<BasicEasterEggUnit> basicEasterEggUnits = EasterEggControl.installAll(true);




        String helpText = buildEasterEggHelpText(basicEasterEggUnits, "所有彩蛋使用帮助");
        CTOptionPane.showMessageDialog(null, "所有彩蛋使用帮助", helpText, null, CTOptionPane.INFORMATION_MESSAGE, true);
        System.exit(0);
    }

    /**
     * 构建所有彩蛋的帮助文本
     * @param units 彩蛋单元列表
     * @param title 对话框标题
     * @return 格式化的帮助文本
     */
    private static String buildEasterEggHelpText(List<BasicEasterEggUnit> units, String title) {
        StringBuilder sb = new StringBuilder();
        for (BasicEasterEggUnit unit : units) {
            sb.append("彩蛋ID: ").append(unit.getID()).append("\n")
                    .append("彩蛋版本: ").append(unit.getVersion()).append("\n")
                    .append("彩蛋开发版本: ").append(unit.getTargetVersion()).append("\n")
                    .append("彩蛋使用帮助: \n").append(unit.help()).append("\n").append("\n");
        }
        return sb.toString();
    }

    /**
     * 构建指定彩蛋的帮助文本
     * @param units 彩蛋单元列表
     * @param title 对话框标题
     * @param targetID 目标彩蛋ID
     * @return 格式化的帮助文本
     */
    private static String buildEasterEggHelpText(List<BasicEasterEggUnit> units, String title, String targetID) {
        StringBuilder sb = new StringBuilder();
        for (BasicEasterEggUnit unit : units) {
            if (unit.getID().equals(targetID)) {
                sb.append("彩蛋ID: ").append(unit.getID()).append("\n")
                        .append("彩蛋版本: ").append(unit.getVersion()).append("\n")
                        .append("彩蛋开发版本: ").append(unit.getTargetVersion()).append("\n")
                        .append("彩蛋使用帮助: \n").append(unit.help()).append("\n").append("\n");
            }
        }
        return sb.toString();
    }
}
