package com.godpalace.waiter;

import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Compiler {
    private static final ConfigMgr configMgr = Main.configMgr;
    private static final Map<String, Thread> threads = new HashMap<>();

    private static class ThreadClass implements Runnable {
        String name;

        public ThreadClass(String name) {
            this.name = name;
        }

        @Override
        @SuppressWarnings("all")
        public void run() {
            Robot robot = null;
            try {
                robot = new Robot();
                robot.setAutoDelay(configMgr.getConfig(name).runDelay);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                if (!configMgr.getConfig(name).isRunning) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}

                    continue;
                }
                Command command = configMgr.getConfig(name).command;
                int runDelay = configMgr.getConfig(name).runDelay;
                robot.setAutoDelay(runDelay);

                for (Command.Cmd cmd : command.commands) {
                    String cmdStr = cmd.cmd;
                    Vector<Integer> values = cmd.values;

                    if (cmdStr.equals("PressMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mousePress(InputEvent.BUTTON1_MASK);
                        } else if (value == 2) {
                            robot.mousePress(InputEvent.BUTTON2_MASK);
                        } else if (value == 3) {
                            robot.mousePress(InputEvent.BUTTON3_MASK);
                        }
                    }

                    if (cmdStr.equals("ReleaseMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        } else if (value == 2) {
                            robot.mouseRelease(InputEvent.BUTTON2_MASK);
                        } else if (value == 3) {
                            robot.mouseRelease(InputEvent.BUTTON3_MASK);
                        }
                    }

                    if (cmdStr.equals("ClickMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mousePress(InputEvent.BUTTON1_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        } else if (value == 2) {
                            robot.mousePress(InputEvent.BUTTON2_MASK);
                            robot.mouseRelease(InputEvent.BUTTON2_MASK);
                        } else if (value == 3) {
                            robot.mousePress(InputEvent.BUTTON3_MASK);
                            robot.mouseRelease(InputEvent.BUTTON3_MASK);
                        }
                    }

                    if (cmdStr.equals("PressKey")) {
                        int value = values.get(0);
                        robot.keyPress(value);
                    }

                    if (cmdStr.equals("ReleaseKey")) {
                        int value = values.get(0);
                        robot.keyRelease(value);
                    }

                    if (cmdStr.equals("TypeKey")) {
                        int value = values.get(0);
                        robot.keyPress(value);
                        robot.keyRelease(value);
                    }

                    if (cmdStr.equals("MoveMouse")) {
                        int x = values.get(0);
                        int y = values.get(1);
                        robot.mouseMove(x, y);
                    }

                    if (cmdStr.equals("MoveMouse2")) {
                        int x = values.get(0);
                        int y = values.get(1);
                        int x1 = MouseInfo.getPointerInfo().getLocation().x;
                        int y1 = MouseInfo.getPointerInfo().getLocation().y;
                        robot.mouseMove(x1 + x, y1 + y);
                    }

                    if (cmdStr.equals("Sleep")) {
                        int delay = values.get(0);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if (!configMgr.getConfig(name).isWhile) {
                    configMgr.getConfig(name).isRunning = false;
                }
            }
        }
    }

    public Compiler() {}

    private static String SplitSpace(String str) {
        String[] strs = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String s : strs) {
            result.append(s).append(" ");
        }
        return result.toString().trim();
    }

    public static Command compile(String name, String path) throws Exception {
        Command c = new Command();

        FileInputStream fileInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        String line;

        fileInputStream = new FileInputStream(path);
        inputStreamReader = new InputStreamReader(fileInputStream);
        reader = new BufferedReader(inputStreamReader);

        while ((line = reader.readLine())!= null) {
            line = SplitSpace(line);

            String[] cmds = line.split(":");
            Command.Cmd cmd = new Command.Cmd();
            cmd.cmd = cmds[0];
            String[] values = cmds[1].split(",");

            for (String value : values) {
                cmd.values.add(Integer.parseInt(value));
            }
            c.addCommand(cmd);
        }

        return c;
    }

    public void createThread(String name) throws Exception {
        ThreadClass thread = new ThreadClass(name);
        threads.put(name, new Thread(thread));
    }

    public void executeAll() {
        for (Config config : ConfigMgr.configMap.values()) {
            config.isRunning = true;
            String name = config.name;
            if (!threads.get(name).isAlive()) {
                threads.get(name).start();
            }
        }
    }

    public void execute(String name) {
        configMgr.getConfig(name).isRunning = true;
        if (!configMgr.hasConfig(name)) {
            throw new IllegalArgumentException("No such config: " + name);
        }

        if (!threads.get(name).isAlive()) {
            threads.get(name).start();
        }
    }

    public void stopAll() {
        for (Config config : ConfigMgr.configMap.values()) {
            config.isRunning = false;
        }
    }

    public void stop(String name) {
        configMgr.getConfig(name).isRunning = false;
    }
}
