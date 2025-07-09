package com.godpalace.waiter.gui;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UIFrame extends JFrame {
    public static ConfigPanel configPanel;
    public static FilePanel filePanel;
    public static JTabbedPane tabbedPane = new JTabbedPane();

    public static boolean closeExit = false;
    public static TrayIcon trayIcon;

    public UIFrame() {
        setTitle("Waiter " + Main.VERSION);
        setIconImage(Main.ICON);
        setSize(600, 400);
        setLocation(250, 250);
        addWindowListener(new WindowAdapter() {
            @SneakyThrows
            @Override
            public void windowClosing(WindowEvent e) {
                if (closeExit) {
                    Main.configMgr.save();
                    System.exit(0);
                } else {
                    UIFrame.this.setVisible(false);
                }
            }
        });

        filePanel = new FilePanel();
        configPanel = new ConfigPanel();

        setContentPane(tabbedPane);
        tabbedPane.addTab("配置", configPanel);
        tabbedPane.addTab("文件", filePanel);

        setVisible(true);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("文件");//
        JMenuItem item1 = new JMenuItem("添加配置");
        item1.addActionListener(e -> {
            String path;
            int result = Main.fileChooser.showOpenDialog(Main.frame);
            File file;
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = Main.fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(Main.FILE_TYPE)) {
                JOptionPane.showMessageDialog(Main.frame, "文件格式不正确！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            path = file.getAbsolutePath();

            String initialName = file.getName().substring(0, file.getName().lastIndexOf("."));
            String name = JOptionPane.showInputDialog(Main.frame, "请输入配置名称：", initialName);

            if (name == null || name.isEmpty()) {
                return;
            }
            try {
                Main.configMgr.addConfig(new Config(name, path));
                Main.configMgr.save();
                configPanel.updateConfigPanel();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        menu.add(item1);

        JMenuItem item3 = new JMenuItem("新建配置");
        item3.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(Main.frame, "请输入配置名称：");
            if (name == null || name.isEmpty()) {
                return;
            }
            try {
                String path = name + "." + Main.FILE_TYPE;
                File file = new File(path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                Main.configMgr.addConfig(new Config(name, path));
                configPanel.updateConfigPanel();
                Main.configMgr.save();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        menu.add(item3);

        JMenuItem item2 = new JMenuItem("删除配置");
        item2.addActionListener(e -> {
            String name = configPanel.getSelectedConfigName();
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
            ConfigPanel.settingsPanel.setEnables(false);
            UIFrame.filePanel.setEnables(false);
            configPanel.updateConfigPanel();
        });
        menu.add(item2);

        JMenuItem item4 = new JMenuItem("运行/停止配置");
        item4.addActionListener(e -> {
            String name = configPanel.getSelectedConfigName();
            if (name == null || name.isEmpty()) {
                return;
            }
            if (Main.configMgr.getConfig(name).isRunning) {
                Main.compiler.stop(name);
            } else {
                Main.compiler.execute(name);
            }
        });
        menu.add(item4);

        menu.addSeparator();

        JMenuItem item = new JMenuItem("退出");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);

        menuBar.add(menu);//

        JMenu menu1 = new JMenu("编辑");//

        JMenuItem item5 = new JMenuItem("保存配置文件");
        item5.addActionListener(e -> {
            try {
                filePanel.save();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        menu1.add(item5);

        JMenuItem item13 = new JMenuItem("删除配置文件");
        item13.addActionListener(e -> {
            String name = filePanel.name;
            if (name == null || name.isEmpty()) {
                return;
            }
            int result = JOptionPane.showConfirmDialog(Main.frame, "确认删除配置文件？", "警告", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                File file = new File(Main.configMgr.getConfig(name).path);
                if (file.exists()) {
                    Files.delete(file.toPath());
                }
                UIFrame.filePanel.setName("");
                Main.configMgr.getConfig(name).path = "";
                Main.configMgr.getConfig(name).isRunning = false;
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ConfigPanel.settingsPanel.loadConfig();
        });
        menu1.add(item13);

        JMenuItem item17 = new JMenuItem("彻底删除配置");
        item17.addActionListener(e -> {
            String name = configPanel.getSelectedConfigName();
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
                    Files.delete(file.toPath());
                }

                Main.configMgr.removeConfig(name);
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ConfigPanel.settingsPanel.setEnables(false);
            UIFrame.filePanel.setEnables(false);
            configPanel.updateConfigPanel();
        });
        menu1.add(item17);

        menuBar.add(menu1);//

        JMenu menu2 = new JMenu("设置");//

        JMenuItem item6 = new JMenuItem("修改全局热键");
        item6.addActionListener(e -> {
            String key = JOptionPane.showInputDialog(Main.frame, "请输入热键：", ConfigMgr.enableKey);
            if (key == null || key.isEmpty()) {
                ConfigMgr.enableKey = "None";
            } else {
                ConfigMgr.enableKey = key;
            }
            try {
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        menu2.add(item6);

        JMenuItem item8 = new JMenuItem("修改记录器热键");
        item8.addActionListener(e -> {
            String key = JOptionPane.showInputDialog(Main.frame, "请输入热键：", ConfigMgr.recordkey);
            if (key == null || key.isEmpty()) {
                ConfigMgr.recordkey = "None";
            }
            else {
                ConfigMgr.recordkey = key;
            }
            try {
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        menu2.add(item8);

        JMenuItem item16 = new JMenuItem("修改记录器配置");
        item16.addActionListener(e -> {
            Main.recorder.showSaveFrame();
        });
        menu2.add(item16);

        JMenuItem item11 = new JMenuItem("禁用所有快捷键");
        item11.addActionListener(e -> {
            if (Main.binder.isRunning()) {
                Main.binder.stop();
                trayIcon.displayMessage("Waiter", "所有快捷键已禁用！", TrayIcon.MessageType.INFO);
                item11.setText("启用所有快捷键");
            } else {
                Main.binder.start();
                trayIcon.displayMessage("Waiter", "所有快捷键已启用！", TrayIcon.MessageType.INFO);
                item11.setText("禁用所有快捷键");
            }
        });
        menu2.add(item11);

        JMenuItem item12 = new JMenuItem("最小化到托盘 " + (closeExit ? "[关闭]" : "[开启]"));
        item12.addActionListener(e -> {
            if (closeExit) {
                closeExit = false;
                item12.setText("最小化到托盘 [开启]");
            } else {
                closeExit = true;
                item12.setText("最小化到托盘 [关闭]");
            }
        });
        menu2.add(item12);

        menuBar.add(menu2);//

        JMenu menu4 = new JMenu("功能");//
        JMenuItem item14 = new JMenuItem("运行/停止所有配置");
        item14.addActionListener(e -> {ConfigMgr.AllRunning.set(!ConfigMgr.AllRunning.get());
            if (ConfigMgr.AllRunning.get()) {
                System.out.println(" ");
                System.out.println("[*]<All> Run.");
                try {
                    Main.compiler.executeAll();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("[*]<All> Stop.");
                Main.compiler.stopAll();
            }
        });
        menu4.add(item14);

        JMenuItem item15 = new JMenuItem("启动/停止步骤记录器");
        item15.addActionListener(e -> {
            if (Main.recorder.isRecording()) {
                System.out.println(" ");
                System.out.println("[*]<Record> Stop.");
                Main.recorder.stopRecord();
                Main.recorder.saveRecords();
                Main.frame.toFront();
            } else {
                System.out.println(" ");
                System.out.println("[*]<Record> Start.");
                Main.recorder.startRecord();
                Main.frame.toBack();
            }
        });
        menu4.add(item15);

        menuBar.add(menu4);//

        JMenu menu3 = new JMenu("帮助");//
        JMenuItem item7 = new JMenuItem("说明");
        item7.addActionListener(e -> {
            Main.helper.setVisible(true);
        });
        menu3.add(item7);

        menuBar.add(menu3);//

        setJMenuBar(menuBar);

        //System Tray
        try {
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();
                MenuItem item9 = new MenuItem("显示/隐藏");
                item9.addActionListener(e -> Main.frame.setVisible(!Main.frame.isShowing()));
                popup.add(item9);

                MenuItem item10 = new MenuItem("退出");
                item10.addActionListener(e -> {
                    try {
                        Main.configMgr.save();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.exit(0);
                });
                popup.add(item10);

                trayIcon = new TrayIcon(Main.ICON, "Waiter", popup);
                trayIcon.addActionListener(e -> {
                    Main.frame.setVisible(true);
                    Main.frame.toFront();
                });
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
