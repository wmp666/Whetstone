package com.wmp.whetstone.CTComponent.CTProgressBar;

import javax.swing.*;
import java.awt.*;

public class ModernLoadingDialog extends JDialog {
    private final CircleLoader loader;
    private final JLabel percentageLabel;
    private Timer progressTimer;
    private int progress = 0;

    public ModernLoadingDialog(JFrame parent) {
        super(parent, "正在加载...", true);

        setLayout(new BorderLayout(20, 20));
        setUndecorated(true); // 无边框
        setBackground(new Color(255, 255, 255, 0));

        // 创建圆角面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制圆角矩形背景
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // 绘制边框
                g2d.setColor(new Color(220, 220, 220));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 加载动画
        loader = new CircleLoader();
        loader.setPreferredSize(new Dimension(80, 80));

        // 文本标签
        percentageLabel = new JLabel("0%", SwingConstants.CENTER);
        percentageLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        percentageLabel.setForeground(new Color(0, 120, 212));

        JLabel textLabel = new JLabel("正在处理，请稍候...", SwingConstants.CENTER);
        textLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        textLabel.setForeground(new Color(100, 100, 100));

        // 添加到面板
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(loader, BorderLayout.CENTER);
        centerPanel.add(percentageLabel, BorderLayout.SOUTH);
        centerPanel.setOpaque(false);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(textLabel, BorderLayout.SOUTH);

        add(mainPanel);

        // 设置大小并居中
        pack();
        setLocationRelativeTo(parent);

    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) loader.startAnimation();
    }

    public void setValue(int value) {
        if (loader.isIndeterminate()) return;
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        progress = value;
        percentageLabel.setText(value + "%");
        loader.setValue(value);
    }

    public void setIndeterminate(boolean indeterminate) {
        loader.setIndeterminate(indeterminate);
        this.percentageLabel.setText(indeterminate ? "?%" : "0%");
    }

    public CircleLoader getLoader() {
        return loader;
    }
}
