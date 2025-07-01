package com.godpalace.waiter.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FilePanel extends JPanel {
    private static final String[] VALUES = {
            "PressMouse: [按下鼠标键]",
            "ReleaseMouse: [释放鼠标键]",
            "ClickMouse: [单击鼠标键]",
            "MoveMouse: [移动鼠标]",
            "MoveMouse2: [移动鼠标相对位置]",
            "PressKey: [按下键盘键]",
            "ReleaseKey: [释放键盘键]",
            "TypeKey: [输入键盘键]",
            "Sleep: [等待指定时间]",
    };

    public File file;
    public JTextArea MainPanel = new JTextArea();
    public JLabel Title = new JLabel();
    public JPanel toolPanel = new JPanel();
    public FilePanel() {
        setBackground(new Color(240, 240, 240));
        setLayout(new BorderLayout());

        MainPanel.setLineWrap(true);
        MainPanel.setWrapStyleWord(true);
        MainPanel.setBackground(Color.WHITE);
        MainPanel.setFont(new Font("Arial", Font.PLAIN, 17));

        Title.setFont(new Font("Arial", Font.PLAIN, 14));
        Title.setForeground(Color.DARK_GRAY);
        Title.setHorizontalAlignment(SwingConstants.LEFT);
        Title.setText("*.txt");

        JButton inputButton = new JButton("T");
        inputButton.setToolTipText("输入关键字");
        inputButton.setBackground(Color.WHITE);
        inputButton.setPreferredSize(new Dimension(20, 20));
        JPopupMenu popupMenu = new JPopupMenu();
        for (String value : VALUES) {
            JMenuItem menuItem = new JMenuItem(value);
            menuItem.addActionListener(e -> {
                String text = MainPanel.getText();
                MainPanel.setText(text + value.substring(0, value.indexOf(':')));
            });
            popupMenu.add(menuItem);
        }
        inputButton.addActionListener(e -> {
            popupMenu.show(inputButton, 0, inputButton.getHeight());
        });

        toolPanel.add(inputButton);

        add(Title, BorderLayout.NORTH);
        add(toolPanel, BorderLayout.EAST);
        add(new JScrollPane(MainPanel), BorderLayout.CENTER);
    }

    public void setFile(File file) {
        this.file = file;
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            MainPanel.setText(content);
            Title.setText(file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
