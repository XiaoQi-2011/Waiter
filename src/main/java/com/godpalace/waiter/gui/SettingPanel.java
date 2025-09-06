package com.godpalace.waiter.gui;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class SettingPanel extends JPanel {
    private String name;

    public int height = 140;
    public JLabel titleLabel = new JLabel("设置 []");
    public JSpinner delayTextField = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
    public KeyBindField keyBindTextField = new KeyBindField();
    public JCheckBox isWhileChecked = new JCheckBox("", true);
    public JTextArea fileLabel = new JTextArea("");
    public JButton fileButton;

    public JPanel centerPanel = new JPanel();
    public SettingPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(titleLabel, BorderLayout.NORTH);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setSize(150, height);

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

        keyBindTextField.setEnabled(false);

        JPanel keyBindPanel = new JPanel();
        keyBindPanel.setBackground(Color.WHITE);
        keyBindPanel.setLayout(new BorderLayout());
        keyBindPanel.add(new JLabel("快捷键:"), BorderLayout.WEST);
        keyBindPanel.add(keyBindTextField, BorderLayout.CENTER);
        centerPanel.add(keyBindPanel);

        JPanel filePanel = new JPanel();
        filePanel.setBackground(Color.WHITE);
        filePanel.setLayout(new BorderLayout());
        JPanel fileLabelPanel = new JPanel();
        fileLabelPanel.setBackground(Color.WHITE);
        fileLabelPanel.setLayout(new BorderLayout());
        JPanel fileLabel0 = new JPanel();
        fileLabel0.setLayout(new BoxLayout(fileLabel0, BoxLayout.Y_AXIS));
        fileLabel0.setSize(this.getWidth(), 45);
        fileLabel0.setBackground(Color.WHITE);
        fileLabel0.add(Box.createRigidArea(new Dimension(0, 15)));
        fileLabel0.add(new JLabel("文件路径:"));
        fileLabelPanel.add(fileLabel0, BorderLayout.NORTH);
        filePanel.add(fileLabelPanel, BorderLayout.WEST);

        fileLabel.setEditable(false);
        fileLabel.setLineWrap(true);
        fileLabel.setWrapStyleWord(true);
        fileLabel.setOpaque(false);
        fileLabel.setBackground(Color.WHITE);
        filePanel.add(fileLabel, BorderLayout.CENTER);

        JPanel fileButtonPanel = new JPanel();
        fileButtonPanel.setBackground(Color.WHITE);
        fileButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        fileButton = new JButton("修改");
        fileButton.setPreferredSize(new Dimension(35, 30));
        fileButton.setMargin(new Insets(0, 0, 0, 0));
        fileButton.setBackground(Color.WHITE);
        fileButton.addActionListener(e -> {
            int result = Main.fileChooser.showOpenDialog(Main.frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path;
                File file = Main.fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(Main.FILE_TYPE)) {
                    JOptionPane.showMessageDialog(Main.frame, "文件格式不正确！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                path = file.getAbsolutePath();
                fileLabel.setText(path);
                Config config = Main.configMgr.getConfig(name);
                config.path = path;
                try {
                    UIFrame.filePanel.setName(config.name);
                    UIFrame.filePanel.save();
                    Main.configMgr.save();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        fileButtonPanel.add(fileButton);
        filePanel.add(fileButtonPanel, BorderLayout.EAST);
        centerPanel.add(filePanel);

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
        isWhileChecked.setSelected(config.isWhile);
        keyBindTextField.setText(config.keyBind);
        delayTextField.setValue(config.runDelay);
        File file = new File(config.path);
        fileLabel.setText(file.getAbsolutePath());
    }

    public void saveConfig() throws IOException {
        Config config = Main.configMgr.getConfig(name);
        config.runDelay = (int) delayTextField.getValue();
        config.isWhile = isWhileChecked.isSelected();
        config.keyBind = keyBindTextField.getText();
        Main.configMgr.save();
    }

    private void addAutoSave() {
        delayTextField.addChangeListener(e -> {
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

        keyBindTextField.setStopExecutor(() -> {
            try {
                Main.binder.start();
                saveConfig();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
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

    public void setEnables(boolean enabled) {
        delayTextField.setEnabled(enabled);
        keyBindTextField.setEnabled(enabled);
        isWhileChecked.setEnabled(enabled);
        fileLabel.setEnabled(enabled);
        fileButton.setEnabled(enabled);

        if (!enabled) {
            titleLabel.setText("设置 []");
        }
    }
}
