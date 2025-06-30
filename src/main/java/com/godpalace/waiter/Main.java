package com.godpalace.waiter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static final ConfigMgr configMgr = new ConfigMgr();
    public static final Compiler compiler = new Compiler();
    public static final Binder binder = new Binder();

    public static final AtomicBoolean Test = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
        GlobalScreen.registerNativeHook();

        binder.start();
        configMgr.load(compiler);
        System.out.print("""
                -------------------------------------
                欢迎使用自动挂机辅助器！
                制作者：XiaoQi.
                Company @ 2025 GodPalace.
                -------------------------------------
                """);

        String input;
        while (true) {
            System.out.print("user> ");
            input = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if (input == null) continue;
            String[] arg = input.split(" ");

            if (arg[0].equals("help")){
                System.out.println("""
                命令行参数说明：
                -----------------------------------------
                help                          显示帮助信息
                add <配置名> <文件路径>         加载配置文件(All默认为F6)
                remove <配置名>                移除配置(All表示所有配置)
                bind <配置名> <键值>            绑定键盘按键
                setWhile <配置名> <true/false> 配置是否循环
                list                          显示已加载的配置
                test                          测试功能(按F5开始运行)
                setDelay <配置名> <值>         设置运行间隔(值单位为毫秒，默认10)
                exit                          退出程序
                _______________[Command]__________________
                PressMouse:<键值>     按下鼠标键
                ReleaseMouse:<键值>   释放鼠标键
                ClickMouse:<键值>     单击鼠标键
                MoveMouse:<x>,<y>     移动鼠标
                MoveMouse2:<dx>,<dy>  移动鼠标相对位置
                PressKey:<键值>       按下键盘键
                ReleaseKey:<键值>     释放键盘键
                TypeKey:<键值>        输入键盘键
                Sleep:<毫秒>          等待指定时间
                -------------------------------------------
                """);
                continue;
            }

            if (arg[0].equals("add") && arg.length == 3){
                System.out.println("[*]Loading config file. ");
                String name = arg[1];
                String path = arg[2];
                if (configMgr.hasConfig(name)) {
                    System.out.println("[!]Config file already exists. ");
                    continue;
                }
                configMgr.addConfig(new Config(name, path));
                compiler.createThread(name);
                configMgr.save();
                continue;
            }

            if (arg[0].equals("remove") && arg.length == 2){
                String name = arg[1];
                if (configMgr.hasConfig(name)) {
                    configMgr.removeConfig(name);
                } else {
                    System.out.println("[!]Config file not found. ");
                }
                configMgr.save();
                continue;
            }

            if (arg[0].equals("bind") && arg.length == 3){
                String name = arg[1];
                String keybind = arg[2];
                if (name.equals("All")) {
                    ConfigMgr.Allkey = keybind;
                } else {
                    if (keybind.length() == 1) keybind = keybind.toUpperCase();
                    configMgr.getConfig(name).keybind = keybind;
                }
                configMgr.save();
                continue;
            }

            if (arg[0].equals("setWhile") && arg.length == 3){
                String name = arg[1];
                configMgr.getConfig(name).isWhile = Boolean.parseBoolean(arg[2]);
                configMgr.save();
                continue;
            }

            if (arg[0].equals("list") && arg.length == 1){
                System.out.println("[*]Loaded config files:");
                System.out.println("[All]: " + ConfigMgr.Allkey);
                for (Config config : ConfigMgr.configMap.values()) {
                    String name = config.name;
                    String isWhile = config.isWhile ? "循环" : "单次";
                    String delay = "延迟: " + config.runDelay;
                    String keybind = "bind: " + (config.keybind == null ? "" : config.keybind);
                    System.out.println(name + " " + isWhile + " " + delay + " " + keybind);
                }
                continue;
            }

            if (arg[0].equals("setDelay") && arg.length == 3){
                String name = arg[1];
                configMgr.getConfig(name).runDelay = Integer.parseInt(arg[2]);
                configMgr.save();
                continue;
            }

            if (arg[0].equals("test") && arg.length == 1){
                Test.set(!Test.get());
                if (Test.get()) {
                    TestPower.start();
                    System.out.println("[*]Test Power On.");
                } else {
                    TestPower.stop();
                    System.out.println("[*]Test Power Off.");
                }
                configMgr.save();
                continue;
            }

            if (input.equals("exit") && arg.length == 1) {
                System.exit(0);
            }

            if (!input.isEmpty()) {
                System.out.println("[!]未知命令. 请输入“help”查看命令行参数说明.");
            }
        }
    }
}
