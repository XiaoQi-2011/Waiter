package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.godpalace.waiter.KeyCodeGetter.getKeyEventCode;

public class TestPower {
    public static boolean isTest;
    public static final NativeKeyListener listener = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            if (e == null) return;
            int x = MouseInfo.getPointerInfo().getLocation().x;
            int y = MouseInfo.getPointerInfo().getLocation().y;
            System.out.println("\n--------------------");
            System.out.println("key: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " " + getKeyEventCode(e.getKeyCode()));
            System.out.println("loc:" + x + "," + y);
            System.out.println("___________________");
        }
    };
    public static final NativeKeyListener listener2 = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            if (e == null) return;
            if (e.getKeyCode() == NativeKeyEvent.VC_F5) {
                isRunning.set(!isRunning.get());
                System.out.println(" ");
                if (isRunning.get()) {
                    System.out.println("[*]test: start");
                    GlobalScreen.addNativeKeyListener(listener);
                } else {
                    System.out.println("[*]test: stop");
                    GlobalScreen.removeNativeKeyListener(listener);
                }
            }
        }
    };
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);

    public TestPower() {}

    public static void start() {
        isTest = true;
        GlobalScreen.addNativeKeyListener(listener2);
    }

    public static void stop() {
        isTest = false;
        GlobalScreen.removeNativeKeyListener(listener2);
    }

}

