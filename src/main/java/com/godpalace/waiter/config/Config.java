package com.godpalace.waiter.config;

import com.godpalace.waiter.execute.Command;

public class Config {
    public String name;
    public String path;
    public int runDelay;
    public boolean isWhile;
    public boolean isRunning = false;
    public Command command;
    public String keyBind;

    public Config(String name, String path, int runDelay, boolean isWhile, String keybind) {
        this.name = name;
        this.path = path;
        this.runDelay = runDelay;
        this.isWhile = isWhile;
        this.keyBind = keybind;
    }

    public Config(String name, String path){
        this.name = name;
        this.path = path;
        this.runDelay = 5;
        this.isWhile = true;
        this.keyBind = "None";
    }
}