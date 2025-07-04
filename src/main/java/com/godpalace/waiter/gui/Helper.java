package com.godpalace.waiter.gui;

import com.godpalace.waiter.Main;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Helper extends JFrame {
    public static JTextPane helpText;

    public Helper() {
        setTitle("帮助");
        setSize(500, 500);
        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        helpText = new JTextPane();
        helpText.setEditable(false);
        helpText.setOpaque(false);
        addHelpText("Waiter: 一款用于执行鼠标和键盘操作的软件。 ", Color.BLUE, HelpType.BOLD, 20);
        addHelpText("作者：XiaoQi, PVPkin", Color.ORANGE, HelpType.ITAL, 15);
        addHelpText("公司：GodPalace", Color.ORANGE, HelpType.ITAL, 15);
        addHelpText("版本：" + Main.VERSION + "\n", Color.ORANGE, HelpType.ITAL, 15);

        addHelpText("[!]注意：", Color.RED, HelpType.BOLD_UNDERLINE, 16);
        addHelpText("如果程序卡死或发生闪退请重启程序，若无效可能是因为配置文件语法错误或保存文件(Config.ini)错误，删除或重新编辑即可。\n", Color.BLACK, HelpType.BOLD, 16);

        addHelpText("[i]说明：", new Color(0, 157, 255), HelpType.BOLD_UNDERLINE, 16);
        addHelpText("在[文件]中的文本框右键可以打开快捷输入菜单。", Color.BLACK, HelpType.BOLD, 16);
        addHelpText("[G]按钮获取数字对应的键值，[K]按钮是获取键盘按键，[P]按钮是获取鼠标位置。", Color.BLACK, HelpType.BOLD, 16);
        addHelpText("1是左键，2是右键，3是中键。(鼠标)\n", Color.BLACK, HelpType.BOLD, 16);

        addHelpText("配置文件命令格式：", Color.BLACK, HelpType.BOLD_UNDERLINE, 16);
        addHelpText("""
                PressMouse:<键值>     按下鼠标键
                ReleaseMouse:<键值>   释放鼠标键
                ClickMouse:<键值>     单击鼠标键
                MoveMouse:<x>,<y>     移动鼠标
                MoveMouse2:<dx>,<dy>  移动鼠标相对位置
                PressKey:<键值>       按下键盘键
                ReleaseKey:<键值>     释放键盘键
                ClickKey:<键值>       单击键盘键
                Sleep:<毫秒>          等待指定时间
                """, Color.BLACK, HelpType.BOLD, 16);


        add(new JScrollPane(helpText), BorderLayout.CENTER);
    }

    private void addHelpText(String str, Color color, HelpType type, int fontSize) {
        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setFontFamily(style,"宋体");
        StyleConstants.setForeground(style, color);
        if (type == HelpType.BOLD) {
            StyleConstants.setBold(style, true);
        } else if (type == HelpType.ITAL) {
            StyleConstants.setItalic(style, true);
        } else if (type == HelpType.UNDERLINE) {
            StyleConstants.setUnderline(style, true);
        } else if (type == HelpType.BOLD_ITAL) {
            StyleConstants.setBold(style, true);
            StyleConstants.setItalic(style, true);
        } else if (type == HelpType.BOLD_UNDERLINE) {
            StyleConstants.setBold(style, true);
            StyleConstants.setUnderline(style, true);
        } else if (type == HelpType.ITAL_UNDERLINE) {
            StyleConstants.setItalic(style, true);
            StyleConstants.setUnderline(style, true);
        } else if (type == HelpType.BOLD_ITAL_UNDERLINE) {
            StyleConstants.setBold(style, true);
            StyleConstants.setItalic(style, true);
            StyleConstants.setUnderline(style, true);
        }
        StyleConstants.setFontSize(style, fontSize);

        Document doc = helpText.getDocument();
        str = str + "\n";
        try {
            doc.insertString(doc.getLength(), str, style);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    enum HelpType {
        BOLD, ITAL, UNDERLINE, BOLD_ITAL, BOLD_UNDERLINE, ITAL_UNDERLINE, BOLD_ITAL_UNDERLINE, COMMON
    }
}
