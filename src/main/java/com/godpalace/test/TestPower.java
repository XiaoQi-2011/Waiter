package com.godpalace.test;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestPower {

    public static final NativeKeyListener listener = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            int x = MouseInfo.getPointerInfo().getLocation().x;
            int y = MouseInfo.getPointerInfo().getLocation().y;
            System.out.println("\nkey:" + getKeyEventCode(e.getKeyCode()));
            System.out.println("loc:" + x + "," + y);
            System.out.println(" ");
        }
    };
    public static final NativeKeyListener listener2 = new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_F5) {
                isRunning.set(!isRunning.get());
                if (isRunning.get()) {
                    System.out.println("[i]test: start");
                    GlobalScreen.addNativeKeyListener(listener);
                } else {
                    System.out.println("[i]test: stop");
                    GlobalScreen.removeNativeKeyListener(listener);
                }
            }
        }
    };
    private static final AtomicBoolean isRunning = new AtomicBoolean(false);

    public TestPower() {
    }

    public static void start() {
        GlobalScreen.addNativeKeyListener(listener2);
    }

    public static void stop() {
        GlobalScreen.removeNativeKeyListener(listener2);
    }

    public static int getKeyEventCode(int keyCode) {
        switch (keyCode) {
            case 0:
                return KeyEvent.CHAR_UNDEFINED;
            case 1:
                return KeyEvent.VK_ESCAPE;
            case 2:
                return KeyEvent.VK_1;
            case 3:
                return KeyEvent.VK_2;
            case 4:
                return KeyEvent.VK_3;
            case 5:
                return KeyEvent.VK_4;
            case 6:
                return KeyEvent.VK_5;
            case 7:
                return KeyEvent.VK_6;
            case 8:
                return KeyEvent.VK_7;
            case 9:
                return KeyEvent.VK_8;
            case 10:
                return KeyEvent.VK_9;
            case 11:
                return KeyEvent.VK_0;
            case 12:
                return KeyEvent.VK_MINUS;
            case 13:
                return KeyEvent.VK_EQUALS;
            case 14:
                return KeyEvent.VK_BACK_SPACE;
            case 15:
                return KeyEvent.VK_TAB;
            case 16:
                return KeyEvent.VK_Q;
            case 17:
                return KeyEvent.VK_W;
            case 18:
                return KeyEvent.VK_E;
            case 19:
                return KeyEvent.VK_R;
            case 20:
                return KeyEvent.VK_T;
            case 21:
                return KeyEvent.VK_Y;
            case 22:
                return KeyEvent.VK_U;
            case 23:
                return KeyEvent.VK_I;
            case 24:
                return KeyEvent.VK_O;
            case 25:
                return KeyEvent.VK_P;
            case 26:
                return KeyEvent.VK_OPEN_BRACKET;
            case 27:
                return KeyEvent.VK_CLOSE_BRACKET;
            case 28:
                return KeyEvent.VK_ENTER;
            case 29:
                return KeyEvent.VK_CONTROL;
            case 30:
                return KeyEvent.VK_A;
            case 31:
                return KeyEvent.VK_S;
            case 32:
                return KeyEvent.VK_D;
            case 33:
                return KeyEvent.VK_F;
            case 34:
                return KeyEvent.VK_G;
            case 35:
                return KeyEvent.VK_H;
            case 36:
                return KeyEvent.VK_J;
            case 37:
                return KeyEvent.VK_K;
            case 38:
                return KeyEvent.VK_L;
            case 39:
                return KeyEvent.VK_SEMICOLON;
            case 40:
                return KeyEvent.VK_QUOTE;
            case 41:
                return KeyEvent.VK_BACK_QUOTE;
            case 42:
                return KeyEvent.VK_SHIFT;
            case 43:
                return KeyEvent.VK_BACK_SLASH;
            case 44:
                return KeyEvent.VK_Z;
            case 45:
                return KeyEvent.VK_X;
            case 46:
                return KeyEvent.VK_C;
            case 47:
                return KeyEvent.VK_V;
            case 48:
                return KeyEvent.VK_B;
            case 49:
                return KeyEvent.VK_N;
            case 50:
                return KeyEvent.VK_M;
            case 51:
                return KeyEvent.VK_COMMA;
            case 52:
                return KeyEvent.VK_PERIOD;
            case 53:
                return KeyEvent.VK_SLASH;
            case 56:
                return KeyEvent.VK_ALT;
            case 57:
                return KeyEvent.VK_SPACE;
            case 58:
                return KeyEvent.VK_CAPS_LOCK;
            case 59:
                return KeyEvent.VK_F1;
            case 60:
                return KeyEvent.VK_F2;
            case 61:
                return KeyEvent.VK_F3;
            case 62:
                return KeyEvent.VK_F4;
            case 63:
                return KeyEvent.VK_F5;
            case 64:
                return KeyEvent.VK_F6;
            case 65:
                return KeyEvent.VK_F7;
            case 66:
                return KeyEvent.VK_F8;
            case 67:
                return KeyEvent.VK_F9;
            case 68:
                return KeyEvent.VK_F10;
            case 69:
                return KeyEvent.VK_NUM_LOCK;
            case 70:
                return KeyEvent.VK_SCROLL_LOCK;
            case 83:
                return KeyEvent.VK_NUMPAD0;
            case 87:
                return KeyEvent.VK_F11;
            case 88:
                return KeyEvent.VK_F12;
            case 91:
                return KeyEvent.VK_F13;
            case 92:
                return KeyEvent.VK_F14;
            case 93:
                return KeyEvent.VK_F15;
            case 99:
                return KeyEvent.VK_F16;
            case 100:
                return KeyEvent.VK_F17;
            case 101:
                return KeyEvent.VK_F18;
            case 102:
                return KeyEvent.VK_F19;
            case 103:
                return KeyEvent.VK_F20;
            case 104:
                return KeyEvent.VK_F21;
            case 105:
                return KeyEvent.VK_F22;
            case 106:
                return KeyEvent.VK_F23;
            case 107:
                return KeyEvent.VK_F24;
            case 112:
                return KeyEvent.VK_KATAKANA;
            case 115:
                return KeyEvent.VK_UNDERSCORE;
            case 119:
                return KeyEvent.VK_KANA;
            case 121:
                return KeyEvent.VK_KANJI;
            case 123:
                return KeyEvent.VK_HIRAGANA;
            case 3639:
                return KeyEvent.VK_PRINTSCREEN;
            case 3653:
                return KeyEvent.VK_PAUSE;
            case 3655:
                return KeyEvent.VK_HOME;
            case 3657:
                return KeyEvent.VK_PAGE_UP;
            case 3663:
                return KeyEvent.VK_END;
            case 3665:
                return KeyEvent.VK_PAGE_DOWN;
            case 3666:
                return KeyEvent.VK_INSERT;
            case 3667:
                return KeyEvent.VK_DELETE;
            case 57416:
                return KeyEvent.VK_UP;
            case 57419:
                return KeyEvent.VK_LEFT;
            case 57420:
                return KeyEvent.VK_CLEAR;
            case 57421:
                return KeyEvent.VK_RIGHT;
            case 57424:
                return KeyEvent.VK_DOWN;
        }
        return keyCode;
    }
}

