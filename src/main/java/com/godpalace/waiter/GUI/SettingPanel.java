package com.godpalace.waiter.GUI;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

public class SettingPanel extends JPanel {
    private String name;
    public JLabel titleLabel = new JLabel("设置 []");
    public JTextField delayTextField = new JTextField("10");
    public JTextField keyBindTextField = new JTextField(" ");
    public JCheckBox isWhileChecked = new JCheckBox("", true);

    public JPanel centerPanel = new JPanel();
    public SettingPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(titleLabel, BorderLayout.NORTH);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setSize(150, 125);

        JPanel delayPanel = new JPanel();
        delayPanel.setBackground(Color.WHITE);
        delayPanel.setLayout(new BorderLayout());
        delayPanel.add(new JLabel("延迟(刻):"), BorderLayout.WEST);
        delayPanel.add(delayTextField, BorderLayout.CENTER);
        centerPanel.add(delayPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));


        JPanel whilePanel = new JPanel();
        whilePanel.setBackground(Color.WHITE);
        whilePanel.setLayout(new BorderLayout());
        whilePanel.add(new JLabel("循环运行:"));
        isWhileChecked.setBackground(Color.WHITE);
        whilePanel.add(isWhileChecked, BorderLayout.EAST);
        centerPanel.add(whilePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel keyBindPanel = new JPanel();
        keyBindPanel.setBackground(Color.WHITE);
        keyBindPanel.setLayout(new BorderLayout());
        keyBindPanel.add(new JLabel("快捷键:"), BorderLayout.WEST);
        keyBindPanel.add(keyBindTextField, BorderLayout.CENTER);
        centerPanel.add(keyBindPanel);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(centerPanel);
        panel.setBackground(Color.WHITE);

        add(panel, BorderLayout.CENTER);
        addAutoSave();
    }

    public void setConfigName(String name) {
        this.name = name;
    }
    public String getConfigName() {
        return name;
    }

    public void loadConfig() {
        Config config = Main.configMgr.getConfig(name);
        titleLabel.setText("设置 [" + config.name + "]");
        delayTextField.setText(String.valueOf(config.runDelay));
        isWhileChecked.setSelected(config.isWhile);
        keyBindTextField.setText(config.keybind);
    }

    public void saveConfig() throws IOException {
        Config config = Main.configMgr.getConfig(name);
        config.runDelay = Integer.parseInt(delayTextField.getText());
        config.isWhile = isWhileChecked.isSelected();
        config.keybind = keyBindTextField.getText();
        Main.configMgr.save();
    }

    private void addAutoSave() {
        delayTextField.addActionListener(e -> {
            try {
                saveConfig();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        delayTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    saveConfig();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        keyBindTextField.addActionListener(e -> {
            try {
                saveConfig();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        keyBindTextField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                try {
                    saveConfig();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        isWhileChecked.addActionListener(e -> {
            try {
                saveConfig();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
