package com.godpalace.waiter.GUI;

import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ConfigPanel extends JSplitPane {
    public static SettingPanel settingsPanel = new SettingPanel();
    public static JList<String> configList = new JList<>();
    public static DefaultListModel<String> listModel = new DefaultListModel<>();

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
            String configName = configList.getSelectedValue();
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
            }
        }).start();
    }

    public void updateConfigPanel() {
        listModel.clear();
        for (Config config : ConfigMgr.configMap.values()) {
            listModel.addElement(config.name);
        }
        if (!listModel.isEmpty()) {
            configList.setSelectedIndex(0);
        } else {
            settingsPanel.setEnables(false);
        }
    }
}
