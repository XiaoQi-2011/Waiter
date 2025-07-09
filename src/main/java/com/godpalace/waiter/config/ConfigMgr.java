package com.godpalace.waiter.config;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.execute.Compiler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigMgr {
    public static final String TAG = "config.cfg";
    public static String enableKey = "F6";
    public static String recordkey = "F7";
    public static final AtomicBoolean AllRunning = new AtomicBoolean(false);
    public static Map<String, Config> configMap = new HashMap<>();

    public ConfigMgr() {}

    public void addConfig(Config config) throws IOException {
        config.command = Compiler.compile(config.path);
        configMap.put(config.name, config);
        Main.compiler.createThread(config.name);
    }

    public Config getConfig(String name) {
        return configMap.get(name);
    }

    public void removeConfig(String name) {
        configMap.remove(name);
        Main.compiler.removeThread(name);
    }

    public void clearConfig() {
        configMap.clear();
    }

    public boolean hasConfig(String name) {
        return configMap.containsKey(name);
    }

    public void save() throws IOException {
        File file = new File(TAG);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(("Enable " + enableKey + "\n").getBytes());
        fos.write(("Record " + recordkey + "\n").getBytes());
        fos.write(("RecordSetting " + Main.recorder.getSaveString() + "\n").getBytes());
        for (Config config : configMap.values()) {
            String text = "Config " + config.name + " "
                    + config.path + " "
                    + config.runDelay + " "
                    + (config.isWhile? "true" : "false") + " "
                    + (config.keyBind == null ? "None" : config.keyBind);
            fos.write((text +"\n").getBytes());
        }
        fos.close();
    }

    public void load() throws IOException, IllegalArgumentException {
        File file = new File(TAG);
        if (!file.exists()) {
            return;
        }

        FileInputStream fis = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;

        while ((line = reader.readLine())!= null) {
            if (line.startsWith("Enable ")) {
                enableKey = line.split(" ")[1];
                continue;
            }

            if (line.startsWith("Record ")) {
                recordkey = line.split(" ")[1];
                continue;
            }

            if (line.startsWith("RecordSetting ")) {
                String setting = line.split(" ")[1];
                Main.recorder.setSaveString(setting);
                continue;
            }

            if (line.startsWith("Config ")) {
                String[] parts = line.split(" ");
                String name = parts[1];
                String path = parts[2];
                int runDelay = Integer.parseInt(parts[3]);
                boolean isWhile = parts[4].equals("true");
                String keybind = (parts[5] == null ? "None" : parts[5]);
                Config config = new Config(name, path, runDelay, isWhile, keybind);
                addConfig(config);
                continue;
            }

            throw new IllegalArgumentException("语法错误, 在第" + line + "行中");
        }

        fis.close();
    }
}
