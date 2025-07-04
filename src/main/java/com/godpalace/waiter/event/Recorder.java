package com.godpalace.waiter.event;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;
import com.godpalace.waiter.gui.UIFrame;
import com.godpalace.waiter.util.KeyCodeGetter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recorder {
    public final List<String> records = Collections.synchronizedList(new ArrayList<>());

    public boolean recordMouseLocation = true;
    public boolean recordKeyboardInput = true;
    public boolean recordMouseClicks = true;
    public boolean messageTooltip = true;

    private boolean isRecording = false;

    private void KeyEvent(String type, NativeKeyEvent nativeEvent) {
        if (recordKeyboardInput) {
            int keyCode = nativeEvent.getKeyCode();
            if (NativeKeyEvent.getKeyText(keyCode).equals(ConfigMgr.recordkey)) {
                return;
            }
            keyCode = KeyCodeGetter.getKeyEventCode(keyCode);
            String key = String.format(type + ":%s", keyCode);
            records.add(key);
        }
    }

    private void MouseEvent(String type, NativeMouseEvent nativeEvent) {
        if (recordMouseLocation) {
            int x = nativeEvent.getX();
            int y = nativeEvent.getY();
            String location = String.format("MoveMouse:%s,%s", x, y);
            records.add(location);
        }
        if (recordMouseClicks) {
            int button = nativeEvent.getButton();
            if (button > 3 || button < 1) return;
            String click = String.format(type + ":%s", button);
            records.add(click);
        }
    }

    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            KeyEvent("PressKey", nativeEvent);
        }


        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            KeyEvent("ReleaseKey", nativeEvent);
        }
    };

    private final NativeMouseListener mouseListener = new NativeMouseListener() {
        @Override
        public void nativeMousePressed(NativeMouseEvent nativeEvent) {
            MouseEvent("PressMouse", nativeEvent);
        }

        @Override
        public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
            MouseEvent("ClickMouse", nativeEvent);
        }

        @Override
        public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
            MouseEvent("ReleaseMouse", nativeEvent);
        }
    };

    public Recorder() {}

    public void startRecord() {
        isRecording = true;
        if (messageTooltip) {
            UIFrame.trayIcon.displayMessage("Waiter", "步骤记录器启动", TrayIcon.MessageType.INFO);
        }
        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
    }

    public void stopRecord() {
        isRecording = false;
        if (messageTooltip) {
            UIFrame.trayIcon.displayMessage("Waiter", "步骤记录器停止", TrayIcon.MessageType.INFO);
        }

        GlobalScreen.removeNativeKeyListener(keyListener);
        GlobalScreen.removeNativeMouseListener(mouseListener);
    }

    public String getRecordText() {
        StringBuilder result = new StringBuilder();

        for (String record : records) {
            result.append(record).append("\n");
        }

        return result.toString();
    }

    public void saveRecords() {
        String records = getRecordText();
        String fileName = "Record_" + System.currentTimeMillis() + "." + Main.FILE_TYPE;
        String filePath = JOptionPane.showInputDialog("输入保存文件路径:", fileName);

        if (filePath == null) {
            return;
        }

        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            Files.write(file.toPath(), records.getBytes());
            JOptionPane.showMessageDialog(Main.frame, "保存成功！");

            Main.configMgr.addConfig(new Config(fileName.substring(0, fileName.lastIndexOf('.')), filePath));
            Main.configMgr.save();
            UIFrame.configPanel.updateConfigPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Main.frame, "保存失败！");
            throw new RuntimeException(e);
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
