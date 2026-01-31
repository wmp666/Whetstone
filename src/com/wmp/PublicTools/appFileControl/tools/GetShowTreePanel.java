package com.wmp.PublicTools.appFileControl.tools;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;

public class GetShowTreePanel {
    /**
     * 获取显示树形结构面板
     *
     * @param data 数据集,每个数据中父级和子级之间用"."分隔
     * @return JTree
     */
    public static JTree getShowTreePanel(String[] data, String rootName) {
        DefaultMutableTreeNode root = buildTreeFromData(data, rootName);

        return new JTree(root);
    }

    private static DefaultMutableTreeNode buildTreeFromData(String[] data, String rootName) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);
        Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();

        for (String path : data) {
            String[] parts = path.split("\\.");
            DefaultMutableTreeNode parentNode = root;

            // 遍历路径的每个部分，构建树形结构
            StringBuilder currentPath = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    currentPath.append(".");
                }
                currentPath.append(parts[i]);

                String fullPath = currentPath.toString();

                // 检查是否已存在该节点
                if (!nodeMap.containsKey(fullPath)) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(parts[i]);
                    nodeMap.put(fullPath, node);
                    parentNode.add(node);
                }

                parentNode = nodeMap.get(fullPath);
            }
        }
        System.out.println(nodeMap);
        return root;
    }
}
