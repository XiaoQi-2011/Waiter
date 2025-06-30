package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.godpalace.waiter.GUI.UIFrame;
import com.godpalace.waiter.config.ConfigMgr;
import com.godpalace.waiter.execute.Compiler;


public class Main {
    public static final ConfigMgr configMgr = new ConfigMgr();
    public static final Compiler compiler = new Compiler();
    public static final Binder binder = new Binder();

    public static UIFrame frame;
    public static void main(String[] args) throws Exception {
        GlobalScreen.registerNativeHook();

        binder.start();
        configMgr.load(compiler);

        frame = new UIFrame();
        frame.setVisible(true);
    }
}
