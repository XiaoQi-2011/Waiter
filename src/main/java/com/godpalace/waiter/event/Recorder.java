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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recorder {
    public final List<String> records = Collections.synchronizedList(new ArrayList<>());

    private boolean recordMouseLocation = true;
    private boolean recordKeyboardInput = true;
    private boolean recordMouseClicks = true;

    private boolean recordSleep = false;
    private int minSleepTime = 500;

    private boolean AutoRecordMove = false;
    private int AutoRecordMoveTime = 200;

    private boolean messageTooltip = true;

    private boolean isRecording = false;
    private int sleepTime = 0;
    private final Timer timer = new Timer(1, e -> sleepTime++);
    private final JFrame frame = new JFrame("步骤记录器配置修改");

    private void KeyEvent(String type, NativeKeyEvent nativeEvent) {
        if (recordSleep) {
            if (type.equals("PressKey")) {
                sleepTime = 0;
                timer.restart();
            } else if (type.equals("ReleaseKey")) {
                timer.stop();
                if (sleepTime > minSleepTime) {
                    System.out.println("Sleep:" + sleepTime + "ms, " + minSleepTime + "ms");
                    records.add("Sleep:" + sleepTime);
                }
            }
        }
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
        if (recordSleep) {
            if (type.equals("PressMouse")) {
                sleepTime = 0;
                timer.restart();
            } else if (type.equals("ReleaseMouse")) {
                timer.stop();
                if (sleepTime > minSleepTime) {
                    System.out.println("Sleep:" + sleepTime);
                    records.add("Sleep:" + sleepTime);
                }
            }
        }
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

    public Recorder() {
        new Thread(() -> {
            while (true) {
                if (AutoRecordMove) {
                    int x = MouseInfo.getPointerInfo().getLocation().x;
                    int y = MouseInfo.getPointerInfo().getLocation().y;
                    records.add("MoveMouse:" + x + "," + y);
                }
                try {
                    Thread.sleep(AutoRecordMoveTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        initSaveFrame();
    }

    public void startRecord() {
        records.clear();
        isRecording = true;
        if (messageTooltip) {
            UIFrame.trayIcon.displayMessage("Waiter", "步骤记录器启动 (" + ConfigMgr.recordkey + " 关闭)", TrayIcon.MessageType.INFO);
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

    private void initSaveFrame() {
        frame.setSize(320, 250);
        frame.setLocation(350, 350);
        frame.setBackground(Color.WHITE);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JCheckBox recordMouseLocationCheck = new JCheckBox("记录鼠标位置");
        recordMouseLocationCheck.setSelected(recordMouseLocation);
        recordMouseLocationCheck.setBackground(Color.WHITE);
        recordMouseLocationCheck.addActionListener(e -> recordMouseLocation = recordMouseLocationCheck.isSelected());
        panel.add(recordMouseLocationCheck);

        JCheckBox recordMouseClicksCheck = new JCheckBox("记录鼠标点击");
        recordMouseClicksCheck.setSelected(recordMouseClicks);
        recordMouseClicksCheck.setBackground(Color.WHITE);
        recordMouseClicksCheck.addActionListener(e -> recordMouseClicks = recordMouseClicksCheck.isSelected());
        panel.add(recordMouseClicksCheck);

        JCheckBox recordKeyboardInputCheck = new JCheckBox("记录键盘输入");
        recordKeyboardInputCheck.setSelected(recordKeyboardInput);
        recordKeyboardInputCheck.setBackground(Color.WHITE);
        recordKeyboardInputCheck.addActionListener(e -> recordKeyboardInput = recordKeyboardInputCheck.isSelected());
        panel.add(recordKeyboardInputCheck);

        JCheckBox recordSleepCheck = new JCheckBox("记录等待时间(Sleep)");
        recordSleepCheck.setSelected(recordSleep);
        recordSleepCheck.setBackground(Color.WHITE);
        recordSleepCheck.addActionListener(e -> recordSleep = recordSleepCheck.isSelected());
        panel.add(recordSleepCheck);

        JPanel minSleepTimePanel = new JPanel();
        minSleepTimePanel.setLayout(new BorderLayout());
        minSleepTimePanel.setBackground(Color.WHITE);
        minSleepTimePanel.add(new JLabel("最小等待时间:"), BorderLayout.WEST);
        JTextField minSleepTimeField = new JTextField(String.valueOf(minSleepTime));
        minSleepTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = deletChar(minSleepTimeField.getText());
                minSleepTime = Integer.parseInt(text);
            }
        });
        minSleepTimePanel.add(minSleepTimeField, BorderLayout.CENTER);
        panel.add(minSleepTimePanel);

        JCheckBox messageTooltipCheck = new JCheckBox("显示提示信息");
        messageTooltipCheck.setSelected(messageTooltip);
        messageTooltipCheck.setBackground(Color.WHITE);
        messageTooltipCheck.addActionListener(e -> messageTooltip = messageTooltipCheck.isSelected());
        panel.add(messageTooltipCheck);

        JCheckBox AutoRecordMoveCheck = new JCheckBox("自动记录鼠标移动");
        AutoRecordMoveCheck.setSelected(AutoRecordMove);
        AutoRecordMoveCheck.setBackground(Color.WHITE);
        AutoRecordMoveCheck.addActionListener(e -> AutoRecordMove = AutoRecordMoveCheck.isSelected());
        panel.add(AutoRecordMoveCheck);

        JPanel AutoRecordMoveTimePanel = new JPanel();
        AutoRecordMoveTimePanel.setLayout(new BorderLayout());
        AutoRecordMoveTimePanel.setBackground(Color.WHITE);
        AutoRecordMoveTimePanel.add(new JLabel("记录间隔时间:"), BorderLayout.WEST);
        JTextField AutoRecordMoveTimeField = new JTextField(String.valueOf(AutoRecordMoveTime));
        AutoRecordMoveTimeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = deletChar(AutoRecordMoveTimeField.getText());
                System.out.println(text);
                AutoRecordMoveTime = Integer.parseInt(text);
            }
        });
        AutoRecordMoveTimePanel.add(AutoRecordMoveTimeField, BorderLayout.CENTER);
        panel.add(AutoRecordMoveTimePanel);

        frame.setContentPane(panel);
        frame.setVisible(false);
    }

    public void showSaveFrame() {
        frame.setVisible(true);
    }

    public boolean isRecording() {
        return isRecording;
    }

    private String deletChar(String str) {
        StringBuilder result = new StringBuilder();
        str = str.toLowerCase();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                result.append(str.charAt(i));
            }
        }
        if (result.isEmpty()) {
            result.append("0");
        }
        return result.toString();
    }
}
