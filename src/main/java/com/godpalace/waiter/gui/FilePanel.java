package com.godpalace.waiter.gui;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.execute.Command;
import com.godpalace.waiter.execute.Compiler;
import com.godpalace.waiter.util.MouseLocGetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
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
            "ClickKey: [单击键盘键]",
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
                if (MainPanel.getSelectedText() == null) {
                    String text = MainPanel.getText();
                    String replace = value.substring(0, value.indexOf(':') + 1);
                    MainPanel.setText(text + replace);
                } else {
                    String replace = value.substring(0, value.indexOf(':') + 1);
                    MainPanel.replaceSelection(replace);
                }
            });
            popupMenu.add(menuItem);
        }

        MainPanel.setComponentPopupMenu(popupMenu);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    if (!MainPanel.getText().equals(oldContent)) {
                        Title.setText("*" + file.getName());
                        oldContent = MainPanel.getText();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        JButton replaceButton = new JButton("G") {
            @Override
            public JToolTip createToolTip() {
                JToolTip toolTip = new JToolTip();
                toolTip.setBackground(Color.WHITE);
                return toolTip;
            }
        };
        replaceButton.setMargin(new Insets(0, 0, 0, 0));
        replaceButton.setBackground(Color.WHITE);
        replaceButton.setToolTipText("获取数字对应的键值");
        replaceButton.setPreferredSize(new Dimension(20, 20));
        replaceButton.addActionListener(e -> {
            String text = JOptionPane.showInputDialog(Main.frame, "请输入数字：", "获取键值", JOptionPane.PLAIN_MESSAGE);
            if (text!= null) {
                int keyCode = Integer.parseInt(text);
                String key = KeyEvent.getKeyText(keyCode);
                JOptionPane.showMessageDialog(Main.frame, "键值：" + key);
            }
        });
        toolPanel.add(replaceButton);

        JButton getkeyButton = new JButton("K") {
            @Override
            public JToolTip createToolTip() {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText("获取键盘按键");
                toolTip.setBackground(Color.WHITE);
                return toolTip;
            }
        };
        getkeyButton.setMargin(new Insets(0, 0, 0, 0));
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

        JButton getLocButton = new JButton("P") {
            @Override
            public JToolTip createToolTip() {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText("获取鼠标位置");
                toolTip.setBackground(Color.WHITE);
                return toolTip;
            }
        };
        getLocButton.setMargin(new Insets(0, 0, 0, 0));
        getLocButton.setBackground(Color.WHITE);
        getLocButton.setPreferredSize(new Dimension(20, 20));
        getLocButton.addActionListener(e -> MouseLocGetter.outputMouseLoc(MainPanel));
        toolPanel.add(getLocButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(Title, BorderLayout.CENTER);
        topPanel.add(toolPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(MainPanel), BorderLayout.CENTER);
        setEnables(false);
    }

    public void setEnables(boolean enabled) {
        MainPanel.setEnabled(enabled);
        if (!enabled) {
            MainPanel.setText("");
        }
        Title.setText(enabled ? file.getName() : " ");
        toolPanel.setEnabled(enabled);

        for (Component component : toolPanel.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    public void setName(String name) {
        this.name = name;
        if (name == null || name.isEmpty() || !Main.configMgr.hasConfig(name)) {
            setEnables(false);
            return;
        }
        this.file = new File(Main.configMgr.getConfig(name).path);
        if (!file.exists() || file == null) {
            setEnables(false);
            return;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            MainPanel.setText(content);
            Title.setText(file.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setEnables(true);
    }

    public void save() throws Exception {
        if (file == null || name == null || name.isEmpty()) {
            return;
        }

        String content = MainPanel.getText();
        oldContent = content;
        Files.write(file.toPath(), content.getBytes());
        Title.setText(file.getName());

        Config config = Main.configMgr.getConfig(name);
        Command command = Compiler.compile(config.path);
        if (command == null) {
            JOptionPane.showMessageDialog(Main.frame, Compiler.getError(), "[" + name + "] 编译失败", JOptionPane.ERROR_MESSAGE);
        } else {
            config.command = command;
        }
    }
}
