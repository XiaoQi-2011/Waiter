package com.godpalace.waiter.GUI;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import java.awt.*;

public class ConfigPanel extends JSplitPane {
    public static SettingPanel settingsPanel = new SettingPanel();
    public static JList<String> configList = new JList<>();
    public static DefaultListModel<String> listModel = new DefaultListModel<>();

    public ConfigPanel() {
        setLeftComponent(settingsPanel);
        setRightComponent(configList);
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        setDividerLocation(200);
        setContinuousLayout(true);
        setDividerSize(7);

        configList.setModel(listModel);
        configList.addListSelectionListener(e -> {
            if (configList.getSelectedValue() == null) {
                return;
            }
            settingsPanel.setConfigName(configList.getSelectedValue());
            settingsPanel.loadConfig();
        });
        configList.setFixedCellHeight(35);
        configList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        updateConfigPanel();

        new Thread(() -> {
            while (true) {
                settingsPanel.centerPanel.setSize(settingsPanel.getWidth(), 125);
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
        }
    }
}
