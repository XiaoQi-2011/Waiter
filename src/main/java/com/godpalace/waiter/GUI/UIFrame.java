package com.godpalace.waiter.GUI;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class UIFrame extends JFrame {

    public static ConfigPanel configPanel;
    public static FilePanel filePanel;
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public UIFrame() {
        setTitle("Waiter v4.0");
        setSize(600, 400);
        setLocation(250, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
            String path = "";
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(Main.frame);
            File file = null;
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (file == null) {
                    JOptionPane.showMessageDialog(Main.frame, "文件不能为空！");
                    return;
                }
                path = file.getAbsolutePath();
            }
            String initialName = file.getName().substring(0, file.getName().lastIndexOf("."));
            String name = JOptionPane.showInputDialog(Main.frame, "请输入配置名称：", initialName);
            if (name == null || name.isEmpty()) {
                return;
            }
            try {
                Main.configMgr.addConfig(new Config(name, path));
                Main.compiler.createThread(name);
                Main.configMgr.save();
                configPanel.updateConfigPanel();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(Main.frame, "添加成功！");
        });
        menu.add(item1);

        JMenuItem item2 = new JMenuItem("删除配置");
        item2.addActionListener(e -> {
            String name = ConfigPanel.configList.getSelectedValue();
            if (name == null || name.isEmpty()) {
                return;
            }
            int result = JOptionPane.showConfirmDialog(Main.frame, "确认删除配置？", "警告", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return;
            }
            Main.configMgr.removeConfig(name);
            try {
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ConfigPanel.settingsPanel.setEnables(false);
            configPanel.updateConfigPanel();
        });
        menu.add(item2);

        JMenuItem item3 = new JMenuItem("运行/停止配置");
        item3.addActionListener(e -> {
            String name = ConfigPanel.configList.getSelectedValue();
            if (name == null || name.isEmpty()) {
                return;
            }
            if (Main.configMgr.getConfig(name).isRunning) {
                Main.compiler.stop(name);
            } else {
                Main.compiler.execute(name);
            }
        });
        menu.add(item3);

        menu.addSeparator();

        JMenuItem item = new JMenuItem("退出");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);

        menuBar.add(menu);//

        JMenu menu1 = new JMenu("编辑");//

        JMenuItem item4 = new JMenuItem("保存并编译配置");
        item4.addActionListener(e -> {
            try {
                filePanel.save();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        menu1.add(item4);

        menuBar.add(menu1);//

        JMenu menu2 = new JMenu("设置");//

        JMenuItem item5 = new JMenuItem("修改全局热键");
        item5.addActionListener(e -> {
            String key = JOptionPane.showInputDialog(Main.frame, "请输入热键：", ConfigMgr.Allkey);
            if (key == null || key.isEmpty()) {
                ConfigMgr.Allkey = "None";
            } else {
                ConfigMgr.Allkey = key;
            }
            try {
                Main.configMgr.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        menu2.add(item5);

        menuBar.add(menu2);//

        setJMenuBar(menuBar);
    }
}
//