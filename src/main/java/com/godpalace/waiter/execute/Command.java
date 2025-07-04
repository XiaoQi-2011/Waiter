package com.godpalace.waiter.execute;

import java.util.ArrayList;
import java.util.List;

public class Command {
    public static class Cmd {
        public String cmd;
        public List<Integer> values;

        public Cmd() {
            this.cmd = "";
            values = new ArrayList<>();
        }
    }
    List<Cmd> commands;

    public Command() {
        commands = new ArrayList<>();
    }

    public void addCommand(Cmd cmd) {
        commands.add(cmd);
    }

    public Cmd getCommand(String cmd) {
        for(Cmd c : commands) {
            if(c.cmd.equals(cmd)) {
                return c;
            }
        }

        return null;
    }
}
