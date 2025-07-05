package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.godpalace.waiter.event.Binder;
import com.godpalace.waiter.gui.Helper;
import com.godpalace.waiter.event.Recorder;
import com.godpalace.waiter.gui.UIFrame;
import com.godpalace.waiter.config.ConfigMgr;
import com.godpalace.waiter.execute.Compiler;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

public class Main {
    // Constant
    public static final String FILE_TYPE = "wait";
    public static final String VERSION = "v4.4";

    // Event
    public static final Recorder recorder = new Recorder();
    public static final Helper helper = new Helper();
    public static final Binder binder = new Binder();

    // Execute
    public static final ConfigMgr configMgr = new ConfigMgr();
    public static final Compiler compiler = new Compiler();

    // GUI
    public static UIFrame frame;
    public static final JFileChooser fileChooser = new JFileChooser();

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            JOptionPane.showMessageDialog(null, "初始化监听器失败, 请检查防火墙并重启应用(" + e.getMessage() + ")");
        }

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    return f.getName().toLowerCase().endsWith("." + Main.FILE_TYPE);
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

        try {
            configMgr.load();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO异常: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        frame = new UIFrame();
        frame.setVisible(true);
    }
}
