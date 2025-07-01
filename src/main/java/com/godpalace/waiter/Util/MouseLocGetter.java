package com.godpalace.waiter.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MouseLocGetter {
    public static final class MouseLoc {
        public int x, y;
    }

    public static MouseLoc getMouseLoc() {
        MouseLoc loc = new MouseLoc();

        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setLocation(0, 0);
        frame.setFocusable(true);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setBackground(new Color(0, 0, 0, 50));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setType(JFrame.Type.UTILITY);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                loc.x = MouseInfo.getPointerInfo().getLocation().x;
                loc.y = MouseInfo.getPointerInfo().getLocation().y;

                frame.dispose();
                frame.setVisible(false);
            }
        });

        frame.setVisible(true);

        return loc;
    }
}
