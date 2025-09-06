package com.godpalace.waiter.event;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;
import lombok.Getter;

public class Binder {

    public Binder() {}
    @Getter
    private boolean isRunning = false;

    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            String key = NativeKeyEvent.getKeyText(e.getKeyCode());
            keyEvent(key);
        }
    };

    private final NativeMouseListener mouseListener = new NativeMouseListener() {
        @Override
        public void nativeMouseClicked(NativeMouseEvent e) {
            String key = String.format("BUTTON%d", e.getButton());
            keyEvent(key);
        }
    };

    private void keyEvent(String key) {
        long time = System.currentTimeMillis();
        if (key.equals(ConfigMgr.enableKey)) {
            ConfigMgr.AllRunning.set(!ConfigMgr.AllRunning.get());
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
            System.out.println("(" + (System.currentTimeMillis() - time) + "ms)");
        }
        if (key.equals(ConfigMgr.recordkey)) {
            if (Main.recorder.isRecording()) {
                System.out.println(" ");
                System.out.println("[*]<Record> Stop.");
                Main.recorder.stopRecord();
                Main.recorder.saveRecords();
            } else {
                System.out.println(" ");
                System.out.println("[*]<Record> Start.");
                Main.recorder.startRecord();
            }
            System.out.println("(" + (System.currentTimeMillis() - time) + "ms)");
        }
        for (Config config : ConfigMgr.configMap.values()) {
            if (key.equals(config.keyBind)) {
                System.out.println(" ");
                if (config.isRunning) {
                    System.out.println("[*]<" + config.name + "> Stop.");
                    Main.compiler.stop(config.name);
                } else {
                    System.out.println("[*]<" + config.name + "> Run.");
                    try {
                        Main.compiler.execute(config.name);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                System.out.println("(" + (System.currentTimeMillis() - time) + "ms)");
            }
        }
    }

    public void start(){
        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
        isRunning = true;
    }

    public void stop(){
        GlobalScreen.removeNativeKeyListener(keyListener);
        GlobalScreen.removeNativeMouseListener(mouseListener);
        isRunning = false;
    }
}
