package com.godpalace.waiter.gui;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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

        configList.setComponentPopupMenu(createPopupMenu());
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
                //SettingsPanel
                settingsPanel.centerPanel.setSize(settingsPanel.getWidth(),
                        settingsPanel.height + settingsPanel.fileLabel.getPreferredSize().height);
                settingsPanel.keyBindTextField.setEnabled(Main.binder.isRunning());

                //JList
                for (Config config : ConfigMgr.configMap.values()) {
                    if (!columnNames.contains(config.name)) {
                        continue;
                    }
                    boolean isRunning = listModel.get(columnNames.indexOf(config.name)).endsWith("(运行中)");
                    if (config.isRunning && !isRunning) {
                        listModel.set(columnNames.indexOf(config.name), config.name + " (运行中)");
                    } else if (!config.isRunning && isRunning) {
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

    public String getSelectedConfigName() {
        int index = configList.getSelectedIndex();
        if (index == -1) {
            return null;
        }
        return columnNames.get(index);
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

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();


        JMenuItem item1 = new JMenuItem("运行配置");
        item1.addActionListener(e -> {
            String name = getSelectedConfigName();
            if (name == null || name.isEmpty()) {
                return;
            }
            if (Main.configMgr.getConfig(name).isRunning) {
                Main.compiler.stop(name);
                item1.setText("运行配置");
            } else {
                Main.compiler.execute(name);
                item1.setText("停止配置");
            }
        });
        popupMenu.add(item1);

        JMenuItem item2 = new JMenuItem("删除配置");
        item2.addActionListener(e -> {
            String name = getSelectedConfigName();
            if (name == null || name.isEmpty()) {
                return;
            }
            int result = JOptionPane.showConfirmDialog(Main.frame, "确认删除配置？", "警告", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                Main.configMgr.removeConfig(name);
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            settingsPanel.setEnables(false);
            UIFrame.filePanel.setEnables(false);
            updateConfigPanel();
        });
        popupMenu.add(item2);

        JMenuItem item3 = new JMenuItem("彻底删除配置");
        item3.addActionListener(e -> {
            String name = getSelectedConfigName();
            if (name == null || name.isEmpty()) {
                return;
            }
            int result = JOptionPane.showConfirmDialog(Main.frame, "确认彻底删除配置 (配置+文件)？", "警告", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                File file = new File(Main.configMgr.getConfig(name).path);
                if (file.exists()) {
                    file.delete();
                }

                Main.configMgr.removeConfig(name);
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ConfigPanel.settingsPanel.setEnables(false);
            UIFrame.filePanel.setEnables(false);
            updateConfigPanel();
        });
        popupMenu.add(item3);

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String name = getSelectedConfigName();
                if (name == null || name.isEmpty()) {
                    popupMenu.setVisible(false);
                    return;
                }
                if (Main.configMgr.getConfig(name).isRunning) {
                    item1.setText("停止配置");
                } else {
                    item1.setText("运行配置");
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        return popupMenu;
    }
}
