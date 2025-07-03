package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.godpalace.waiter.GUI.UIFrame;
import com.godpalace.waiter.config.ConfigMgr;
import com.godpalace.waiter.execute.Compiler;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;


public class Main {
    public static final String FILE_TYPE = "wait";
    public static final String VERSION = "v4.3";

    public static final Helper helper = new Helper();
    public static final ConfigMgr configMgr = new ConfigMgr();
    public static final Compiler compiler = new Compiler();
    public static final Binder binder = new Binder();

    public static UIFrame frame;
    public static final JFileChooser fileChooser = new JFileChooser();
    public static void main(String[] args) throws Exception {
        GlobalScreen.registerNativeHook();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith(Main.FILE_TYPE);
                }
            }

            @Override
            public String getDescription() {
                return "配置文件(*." + Main.FILE_TYPE + ")";
            }
        };
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setDialogTitle("选择配置文件");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(filter);

        binder.start();
        configMgr.load(compiler);

        frame = new UIFrame();
        frame.setVisible(true);
    }
}
