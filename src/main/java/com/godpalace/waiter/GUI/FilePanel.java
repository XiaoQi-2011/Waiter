package com.godpalace.waiter.GUI;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.Util.MouseLocGetter;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;
import com.godpalace.waiter.execute.Command;
import com.godpalace.waiter.execute.Compiler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public String name;
    public JTextArea MainPanel = new JTextArea();
    public JLabel Title = new JLabel();
    public JPanel toolPanel = new JPanel();
    private String oldContent = "";
    public FilePanel() {
        setBackground(new Color(246, 246, 246));
        setLayout(new BorderLayout());

        MainPanel.setLineWrap(true);
        MainPanel.setWrapStyleWord(true);
        MainPanel.setBackground(Color.WHITE);
        MainPanel.setFont(new Font("Arial", Font.PLAIN, 17));

        Title.setFont(new Font("Arial", Font.PLAIN, 14));
        Title.setForeground(Color.DARK_GRAY);
        Title.setHorizontalAlignment(SwingConstants.LEFT);
        Title.setText("");

        JPopupMenu popupMenu = new JPopupMenu();
        for (String value : VALUES) {
            JMenuItem menuItem = new JMenuItem(value);
            menuItem.addActionListener(e -> {
                String text = MainPanel.getText();
                MainPanel.setText(text + value.substring(0, value.indexOf(':') + 1));
            });
            popupMenu.add(menuItem);
        }
        popupMenu.addSeparator();

        MainPanel.setComponentPopupMenu(popupMenu);
        MainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!MainPanel.getText().equals(oldContent)) {
                    Title.setText("*" + file.getName());
                    oldContent = MainPanel.getText();
                }
            }
        });

        JButton getkeyButton = new JButton("K");
        getkeyButton.setMargin(new Insets(0, 0, 0, 0));
        getkeyButton.setToolTipText("获取键值");
        getkeyButton.setBackground(Color.WHITE);
        getkeyButton.setPreferredSize(new Dimension(20, 20));
        AtomicBoolean isGetkey = new AtomicBoolean(false);
        getkeyButton.addActionListener(e -> {
            isGetkey.set(!isGetkey.get());
            getkeyButton.setText(isGetkey.get() ? "..." : "K");
        });
        getkeyButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (isGetkey.get()) {
                    MainPanel.setText(MainPanel.getText() + keyCode + "\n");
                    isGetkey.set(!isGetkey.get());
                    getkeyButton.setText(isGetkey.get() ? "..." : "K");
                }
            }
        });
        toolPanel.add(getkeyButton);

        JButton getLocButton = new JButton("L");
        getLocButton.setMargin(new Insets(0, 0, 0, 0));
        getLocButton.setToolTipText("获取鼠标位置");
        getLocButton.setBackground(Color.WHITE);
        getLocButton.setPreferredSize(new Dimension(20, 20));
        getLocButton.addActionListener(e -> {
            MouseLocGetter.outputMouseLoc(MainPanel);
        });
        toolPanel.add(getLocButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(Title, BorderLayout.CENTER);
        topPanel.add(toolPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(MainPanel), BorderLayout.CENTER);
    }

    public void setName(String name) {
        this.name = name;
        this.file = new File(Main.configMgr.getConfig(name).path);
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            MainPanel.setText(content);
            Title.setText(file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws Exception {
        if (file == null || name.isEmpty()) {
            return;
        }
        String content = MainPanel.getText();
        oldContent = content;
        String path = file.getAbsolutePath();
        Files.write(file.toPath(), content.getBytes());
        Main.configMgr.getConfig(name).command = Compiler.compile(path);
        Main.configMgr.save();
        Title.setText(file.getName());
    }
}
//