package com.godpalace.waiter.execute;

import java.util.Vector;

public class Command {
    public static class Cmd {
        public String cmd;
        public Vector<Integer> values;

        public Cmd() {
            this.cmd ="";
            values = new Vector<Integer>();
        }
    }
    Vector<Cmd> commands;

    public Command() {
        commands = new Vector<Cmd>();
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
//