package com.godpalace.waiter.gui;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.godpalace.waiter.Main;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class KeyBindField extends JButton {
    @Getter
    private String keyBind;
    @Setter
    private Runnable stopExecutor;

    private boolean isSelecting = false;
    public KeyBindField() {
        setBackground(Color.WHITE);
        setFocusPainted(false);
        setText("None");
        addActionListener(e -> {
            if (!isSelecting) {
                start();
            }
        });
    }

    private void start() {
        isSelecting = true;
        setText("...");
        Main.binder.stop();
        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
    }

    private void stop() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setText(keyBind.equals("Esc")? "None" : keyBind);
        GlobalScreen.removeNativeKeyListener(keyListener);
        GlobalScreen.removeNativeMouseListener(mouseListener);
        stopExecutor.run();
        isSelecting = false;
    }

    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            String value = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());
            keyBind = value;
            stop();
        }
    };

    private final NativeMouseListener mouseListener = new NativeMouseListener() {
        @Override
        public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
            String value = String.valueOf(nativeEvent.getButton());
            keyBind = "BUTTON" + value;
            stop();
        }
    };
}
