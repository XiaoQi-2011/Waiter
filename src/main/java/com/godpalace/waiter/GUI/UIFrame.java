package com.godpalace.waiter.GUI;

import javax.swing.*;

public class UIFrame extends JFrame {

    public static ConfigPanel configPanel;
    public static FilePanel filePanel;
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public UIFrame() {
        setTitle("Waiter v4.0");
        setSize(600, 400);
        setLocation(250, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        configPanel = new ConfigPanel();
        filePanel = new FilePanel();

        setContentPane(tabbedPane);
        tabbedPane.addTab("配置", configPanel);
        tabbedPane.addTab("文件", filePanel);

        setVisible(true);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("文件");//
        JMenuItem item = new JMenuItem("退出");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);
        menuBar.add(menu);//

        JMenu menu1 = new JMenu("设置");//
        menuBar.add(menu1);//

        JMenu menu2 = new JMenu("编辑");//
        menuBar.add(menu2);//

        setJMenuBar(menuBar);


    }
}
