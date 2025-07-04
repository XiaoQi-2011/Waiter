package com.godpalace.waiter.gui;

import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class ConfigPanel extends JSplitPane {
    public static SettingPanel settingsPanel = new SettingPanel();
    public static JList<String> configList = new JList<>();
    public static DefaultListModel<String> listModel = new DefaultListModel<>();

    private static final Vector<String> columnNames = new Vector<>();
    public ConfigPanel() {
        setLeftComponent(new JScrollPane(settingsPanel));
        setRightComponent(new JScrollPane(configList));
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        setDividerLocation(200);
        setContinuousLayout(true);
        setDividerSize(7);

        configList.setModel(listModel);
        configList.addListSelectionListener(e -> {
            if (configList.getSelectedValue() == null) {
                return;
            }
            settingsPanel.setEnables(true);
            String configName = columnNames.get(configList.getSelectedIndex());
            UIFrame.filePanel.setName(configName);
            settingsPanel.setConfigName(configName);
            settingsPanel.loadConfig();

        });
        configList.setFixedCellHeight(35);
        configList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        updateConfigPanel();

        new Thread(() -> {
            while (true) {
                settingsPanel.centerPanel.setSize(settingsPanel.getWidth(),
                        settingsPanel.height + settingsPanel.fileLabel.getPreferredSize().height);
                for (Config config : ConfigMgr.configMap.values()) {
                    if (config.isRunning) {
                        listModel.set(columnNames.indexOf(config.name), config.name + " (运行中)");
                    } else {
                        listModel.set(columnNames.indexOf(config.name), config.name);
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void updateConfigPanel() {
        listModel.clear();
        columnNames.clear();
        for (Config config : ConfigMgr.configMap.values()) {
            listModel.addElement(config.name);
            columnNames.add(config.name);
        }
        if (!listModel.isEmpty()) {
            configList.setSelectedIndex(0);
        } else {
            settingsPanel.setEnables(false);
        }
    }
}
