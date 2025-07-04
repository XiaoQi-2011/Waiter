package com.godpalace.waiter.event;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

public class Binder {

    public Binder() {}

    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            String key = NativeKeyEvent.getKeyText(e.getKeyCode());
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
                }
            }
        }
    };

    public void start(){
        GlobalScreen.addNativeKeyListener(keyListener);
    }

    public void stop(){
        GlobalScreen.removeNativeKeyListener(keyListener);
    }
}
