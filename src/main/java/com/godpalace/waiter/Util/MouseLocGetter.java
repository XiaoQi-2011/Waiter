package com.godpalace.waiter.Util;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MouseLocGetter {
    public static final class MouseLoc {
        public int x, y;
    }

    public static void outputMouseLoc(JTextComponent textComponent) {
        JFrame frame = new JFrame();
        MouseLoc loc = new MouseLoc();

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
                if (e.getButton() == MouseEvent.BUTTON1) {
                    loc.x = MouseInfo.getPointerInfo().getLocation().x;
                    loc.y = MouseInfo.getPointerInfo().getLocation().y;

                    String text = textComponent.getText();
                    textComponent.setText(text + loc.x + "," + loc.y);
                    frame.dispose();
                }
            }
        });

        JLabel label = new JLabel("点击左键获取鼠标位置");
        label.setForeground(Color.BLUE);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(frame.getWidth() / 2 - 70, frame.getHeight() / 2 - 10, 140, 20);
        label.setFocusable(false);

        frame.add(label);

        frame.setVisible(true);
    }
}
//