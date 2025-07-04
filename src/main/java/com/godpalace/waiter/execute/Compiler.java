package com.godpalace.waiter.execute;

import com.godpalace.waiter.Main;
import com.godpalace.waiter.config.Config;
import com.godpalace.waiter.config.ConfigMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Compiler {
    private static String Error;
    private static final ConfigMgr configMgr = Main.configMgr;
    private static final Map<String, Thread> threads = new HashMap<>();
    private static final String[] COMMANDS = {
            "PressMouse",
            "ReleaseMouse",
            "ClickMouse",
            "PressKey",
            "ReleaseKey",
            "ClickKey",
            "MoveMouse",
            "MoveMouse2",
            "Sleep"
    };

    private static class ThreadClass implements Runnable {
        String name;

        public ThreadClass(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Robot robot;
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
                if (command == null) {
                    configMgr.getConfig(name).isRunning = false;
                    continue;
                }
                int runDelay = configMgr.getConfig(name).runDelay;
                robot.setAutoDelay(runDelay);

                for (Command.Cmd cmd : command.commands) {
                    String cmdStr = cmd.cmd;
                    Vector<Integer> values = cmd.values;

                    if (cmdStr.equals("PressMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        } else if (value == 2) {
                            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                        } else if (value == 3) {
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        }
                    }

                    if (cmdStr.equals("ReleaseMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        } else if (value == 2) {
                            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                        } else if (value == 3) {
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        }
                    }

                    if (cmdStr.equals("ClickMouse")) {
                        int value = values.get(0);
                        if (value == 1) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        } else if (value == 2) {
                            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                        } else if (value == 3) {
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
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

                    if (cmdStr.equals("ClickKey")) {
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

    public static Command compile(String path) throws Exception {
        Command c = new Command();
        ErrorMgr errorMgr = new ErrorMgr();

        FileInputStream fileInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        String line;

        if (!new File(path).exists()) {
            return null;
        }
        fileInputStream = new FileInputStream(path);
        inputStreamReader = new InputStreamReader(fileInputStream);
        reader = new BufferedReader(inputStreamReader);

        int lineNum = 0;
        while ((line = reader.readLine())!= null) {
            lineNum++;
            line = SplitSpace(line);

            String[] cmds = line.split(":");
            Command.Cmd cmd = new Command.Cmd();
            if (cmds.length != 2) {
                errorMgr.addError(lineNum, ErrorMgr.ErrorType.ERROR_STRUCTURE);
                continue;
            }
            cmd.cmd = cmds[0];
            boolean isCommand = false;
            for (String command : COMMANDS) {
                if (cmd.cmd.equals(command)) {
                    isCommand = true;
                    break;
                }
            }
            if (!isCommand) {
                errorMgr.addError(lineNum, ErrorMgr.ErrorType.ERROR_COMMAND);
                continue;
            }
            String[] values = cmds[1].split(",");

            for (String value : values) {
                int v;
                try {
                    v = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    errorMgr.addError(lineNum, ErrorMgr.ErrorType.ERROR_VALUE);
                    break;
                }
                cmd.values.add(v);
            }
            c.addCommand(cmd);
        }

        if (errorMgr.isError()) {
            Error = errorMgr.getErrorMessage();
            return null;
        } else {
            return c;
        }
    }

    public static String getError() {
        return Error;
    }

    public void createThread(String name) {
        ThreadClass thread = new ThreadClass(name);
        threads.put(name, new Thread(thread));
    }

    public void removeThread(String name) {
        threads.get(name).interrupt();
        threads.remove(name);
    }

    public void executeAll() {
        for (Config config : ConfigMgr.configMap.values()) {
            execute(config.name);
        }
    }

    public void execute(String name) {
        if (configMgr.getConfig(name).command == null) {
            JOptionPane.showMessageDialog(null, "运行失败，请检查配置文件是否正确！", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
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
            stop(config.name);
        }
    }

    public void stop(String name) {
        configMgr.getConfig(name).isRunning = false;
    }
}
//