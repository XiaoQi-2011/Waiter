package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;


public class Binder {
    private static final Compiler compiler = Main.compiler;

    public Binder() {}

    private final NativeKeyListener keyListener = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            String key = NativeKeyEvent.getKeyText(e.getKeyCode());
            if (key.equals(ConfigMgr.Allkey)) {
                ConfigMgr.AllRunning.set(!ConfigMgr.AllRunning.get());
                if (ConfigMgr.AllRunning.get()) {
                    System.out.println(" ");
                    System.out.println("[*]<All> Run.");
                    try {
                        compiler.executeAll();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    System.out.println("[*]<All> Stop.");
                    compiler.stopAll();
                }
            }
            for (Config config : ConfigMgr.configMap.values()) {
                if (key.equals(config.keybind)) {
                    System.out.println(" ");
                    if (config.isRunning) {
                        System.out.println("[*]<" + config.name + "> Stop.");
                        compiler.stop(config.name);
                    } else {
                        System.out.println("[*]<" + config.name + "> Run.");
                        try {
                            compiler.execute(config.name);
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
