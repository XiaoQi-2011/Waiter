package com.godpalace.waiter.config;

import com.godpalace.waiter.Compiler;
import com.godpalace.waiter.Main;
import com.godpalace.waiter.TestPower;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigMgr {
    public static final String TAG = "config.ini";
    public static String Allkey = "F6";
    public static final AtomicBoolean AllRunning = new AtomicBoolean(false);
    public static Map<String, Config> configMap = new HashMap<>();

    public ConfigMgr() {}

    public void addConfig(Config config) throws Exception {
        config.command = Compiler.compile(config.name, config.path);
        configMap.put(config.name, config);
    }

    public Config getConfig(String name) {
        return configMap.get(name);
    }

    public void removeConfig(String name) {
        configMap.remove(name);
    }

    public void clearConfig() {
        configMap.clear();
    }

    public boolean hasConfig(String name) {
        return configMap.containsKey(name);
    }

    public void save() throws IOException {
        boolean isTest = TestPower.isTest;
        File file = new File(TAG);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);

        fos.write(("isTest " + (isTest? "true" : "false") + "\n").getBytes());
        fos.write(("All " + Allkey + "\n").getBytes());
        for (Config config : configMap.values()) {
            String text = "Config " + config.name + " "
                    + config.path + " "
                    + config.runDelay + " "
                    + (config.isWhile? "true" : "false") + " "
                    + (config.keybind == null? "none" : config.keybind);
            fos.write((text +"\n").getBytes());
        }
        fos.close();
    }

    public void load(Compiler compiler) throws Exception {
        boolean isTest = TestPower.isTest;
        File file = new File(TAG);
        if (!file.exists()) {
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;

        while ((line = reader.readLine())!= null) {

            if (line.startsWith("isTest ")) {
                isTest = line.split(" ")[1].equals("true");
            }
            if (line.startsWith("All ")) {
                Allkey = line.split(" ")[1];
            }
            if (line.startsWith("Config ")) {
                String[] parts = line.split(" ");
                String name = parts[1];
                String path = parts[2];
                int runDelay = Integer.parseInt(parts[3]);
                boolean isWhile = parts[4].equals("true");
                String keybind = parts[5].equals("none")? null : parts[5];
                Config config = new Config(name, path, runDelay, isWhile, keybind);
                addConfig(config);
            }
        }
        fis.close();
        Main.Test.set(isTest);
        if (isTest) {
            TestPower.start();
        }
        for (Config config : configMap.values()){
            compiler.createThread(config.name);
        }
    }
}
