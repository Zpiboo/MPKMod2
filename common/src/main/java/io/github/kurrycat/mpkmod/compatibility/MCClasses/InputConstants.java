package io.github.kurrycat.mpkmod.compatibility.MCClasses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class InputConstants {
    public static final int KEY_0 = 48;
    public static final int KEY_1 = 49;
    public static final int KEY_2 = 50;
    public static final int KEY_3 = 51;
    public static final int KEY_4 = 52;
    public static final int KEY_5 = 53;
    public static final int KEY_6 = 54;
    public static final int KEY_7 = 55;
    public static final int KEY_8 = 56;
    public static final int KEY_9 = 57;
    public static final int KEY_A = 65;
    public static final int KEY_B = 66;
    public static final int KEY_C = 67;
    public static final int KEY_D = 68;
    public static final int KEY_E = 69;
    public static final int KEY_F = 70;
    public static final int KEY_G = 71;
    public static final int KEY_H = 72;
    public static final int KEY_I = 73;
    public static final int KEY_J = 74;
    public static final int KEY_K = 75;
    public static final int KEY_L = 76;
    public static final int KEY_M = 77;
    public static final int KEY_N = 78;
    public static final int KEY_O = 79;
    public static final int KEY_P = 80;
    public static final int KEY_Q = 81;
    public static final int KEY_R = 82;
    public static final int KEY_S = 83;
    public static final int KEY_T = 84;
    public static final int KEY_U = 85;
    public static final int KEY_V = 86;
    public static final int KEY_W = 87;
    public static final int KEY_X = 88;
    public static final int KEY_Y = 89;
    public static final int KEY_Z = 90;
    public static final int KEY_F1 = 290;
    public static final int KEY_F2 = 291;
    public static final int KEY_F3 = 292;
    public static final int KEY_F4 = 293;
    public static final int KEY_F5 = 294;
    public static final int KEY_F6 = 295;
    public static final int KEY_F7 = 296;
    public static final int KEY_F8 = 297;
    public static final int KEY_F9 = 298;
    public static final int KEY_F10 = 299;
    public static final int KEY_F11 = 300;
    public static final int KEY_F12 = 301;
    public static final int KEY_F13 = 302;
    public static final int KEY_F14 = 303;
    public static final int KEY_F15 = 304;
    public static final int KEY_F16 = 305;
    public static final int KEY_F17 = 306;
    public static final int KEY_F18 = 307;
    public static final int KEY_F19 = 308;
    public static final int KEY_F20 = 309;
    public static final int KEY_F21 = 310;
    public static final int KEY_F22 = 311;
    public static final int KEY_F23 = 312;
    public static final int KEY_F24 = 313;
    public static final int KEY_F25 = 314;
    public static final int KEY_NUMLOCK = 282;
    public static final int KEY_NUMPAD0 = 320;
    public static final int KEY_NUMPAD1 = 321;
    public static final int KEY_NUMPAD2 = 322;
    public static final int KEY_NUMPAD3 = 323;
    public static final int KEY_NUMPAD4 = 324;
    public static final int KEY_NUMPAD5 = 325;
    public static final int KEY_NUMPAD6 = 326;
    public static final int KEY_NUMPAD7 = 327;
    public static final int KEY_NUMPAD8 = 328;
    public static final int KEY_NUMPAD9 = 329;
    public static final int KEY_NUMPADCOMMA = 330;
    public static final int KEY_NUMPADENTER = 335;
    public static final int KEY_NUMPADEQUALS = 336;
    public static final int KEY_DOWN = 264;
    public static final int KEY_LEFT = 263;
    public static final int KEY_RIGHT = 262;
    public static final int KEY_UP = 265;
    public static final int KEY_ADD = 334;
    public static final int KEY_APOSTROPHE = 39;
    public static final int KEY_BACKSLASH = 92;
    public static final int KEY_COMMA = 44;
    public static final int KEY_EQUALS = 61;
    public static final int KEY_GRAVE = 96;
    public static final int KEY_LBRACKET = 91;
    public static final int KEY_MINUS = 45;
    public static final int KEY_MULTIPLY = 332;
    public static final int KEY_PERIOD = 46;
    public static final int KEY_RBRACKET = 93;
    public static final int KEY_SEMICOLON = 59;
    public static final int KEY_SLASH = 47;
    public static final int KEY_SPACE = 32;
    public static final int KEY_TAB = 258;
    public static final int KEY_LALT = 342;
    public static final int KEY_LCONTROL = 341;
    public static final int KEY_LSHIFT = 340;
    public static final int KEY_LWIN = 343;
    public static final int KEY_RALT = 346;
    public static final int KEY_RCONTROL = 345;
    public static final int KEY_RSHIFT = 344;
    public static final int KEY_RWIN = 347;
    public static final int KEY_RETURN = 257;
    public static final int KEY_ESCAPE = 256;
    public static final int KEY_BACKSPACE = 259;
    public static final int KEY_DELETE = 261;
    public static final int KEY_END = 269;
    public static final int KEY_HOME = 268;
    public static final int KEY_INSERT = 260;
    public static final int KEY_PAGEDOWN = 267;
    public static final int KEY_PAGEUP = 266;
    public static final int KEY_CAPSLOCK = 280;
    public static final int KEY_PAUSE = 284;
    public static final int KEY_SCROLLLOCK = 281;
    public static final int KEY_PRINTSCREEN = 283;
    /*public static final int PRESS = 1;
    public static final int RELEASE = 0;
    public static final int REPEAT = 2;
    public static final int MOUSE_BUTTON_LEFT = 0;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    public static final int MOD_CONTROL = 2;
    public static final int CURSOR = 208897;
    public static final int CURSOR_DISABLED = 212995;
    public static final int CURSOR_NORMAL = 212993;*/

    private static final Map<Integer, Integer> LWJGL_TO_GLFW;

    static {
        Map<Integer, Integer> lwjglToGlfw = new HashMap<>();

        for (int lwjglKey = 0x02; lwjglKey <= 0x0A; lwjglKey++)
            lwjglToGlfw.put(lwjglKey, lwjglKey - 0x02 + KEY_1);

        lwjglToGlfw.put(0x01, KEY_ESCAPE);
        lwjglToGlfw.put(0x0B, KEY_0);
        lwjglToGlfw.put(0x0C, KEY_MINUS);
        lwjglToGlfw.put(0x0D, KEY_EQUALS);
        lwjglToGlfw.put(0x0E, KEY_BACKSPACE);
        lwjglToGlfw.put(0x0F, KEY_TAB);
        lwjglToGlfw.put(0x10, KEY_Q);
        lwjglToGlfw.put(0x11, KEY_W);
        lwjglToGlfw.put(0x12, KEY_E);
        lwjglToGlfw.put(0x13, KEY_R);
        lwjglToGlfw.put(0x14, KEY_T);
        lwjglToGlfw.put(0x15, KEY_Y);
        lwjglToGlfw.put(0x16, KEY_U);
        lwjglToGlfw.put(0x17, KEY_I);
        lwjglToGlfw.put(0x18, KEY_O);
        lwjglToGlfw.put(0x19, KEY_P);
        lwjglToGlfw.put(0x1A, KEY_LBRACKET);
        lwjglToGlfw.put(0x1B, KEY_RBRACKET);
        lwjglToGlfw.put(0x1C, KEY_RETURN); /* Enter on main keyboard */
        lwjglToGlfw.put(0x1D, KEY_LCONTROL);
        lwjglToGlfw.put(0x1E, KEY_A);
        lwjglToGlfw.put(0x1F, KEY_S);
        lwjglToGlfw.put(0x20, KEY_D);
        lwjglToGlfw.put(0x21, KEY_F);
        lwjglToGlfw.put(0x22, KEY_G);
        lwjglToGlfw.put(0x23, KEY_H);
        lwjglToGlfw.put(0x24, KEY_J);
        lwjglToGlfw.put(0x25, KEY_K);
        lwjglToGlfw.put(0x26, KEY_L);
        lwjglToGlfw.put(0x27, KEY_SEMICOLON);
        lwjglToGlfw.put(0x28, KEY_APOSTROPHE);
        lwjglToGlfw.put(0x29, KEY_GRAVE); /* accent grave */
        lwjglToGlfw.put(0x2A, KEY_LSHIFT);
        lwjglToGlfw.put(0x2B, KEY_BACKSLASH);
        lwjglToGlfw.put(0x2C, KEY_Z);
        lwjglToGlfw.put(0x2D, KEY_X);
        lwjglToGlfw.put(0x2E, KEY_C);
        lwjglToGlfw.put(0x2F, KEY_V);
        lwjglToGlfw.put(0x30, KEY_B);
        lwjglToGlfw.put(0x31, KEY_N);
        lwjglToGlfw.put(0x32, KEY_M);
        lwjglToGlfw.put(0x33, KEY_COMMA);
        lwjglToGlfw.put(0x34, KEY_PERIOD); /* . on main keyboard */
        lwjglToGlfw.put(0x35, KEY_SLASH); /* / on main keyboard */
        lwjglToGlfw.put(0x36, KEY_RSHIFT);
        lwjglToGlfw.put(0x37, KEY_MULTIPLY); /* * on numeric keypad */
        lwjglToGlfw.put(0x38, KEY_LALT); /* left Alt */
        lwjglToGlfw.put(0x39, KEY_SPACE);
        lwjglToGlfw.put(0x3A, KEY_CAPSLOCK);
        lwjglToGlfw.put(0x3B, KEY_F1);
        lwjglToGlfw.put(0x3C, KEY_F2);
        lwjglToGlfw.put(0x3D, KEY_F3);
        lwjglToGlfw.put(0x3E, KEY_F4);
        lwjglToGlfw.put(0x3F, KEY_F5);
        lwjglToGlfw.put(0x40, KEY_F6);
        lwjglToGlfw.put(0x41, KEY_F7);
        lwjglToGlfw.put(0x42, KEY_F8);
        lwjglToGlfw.put(0x43, KEY_F9);
        lwjglToGlfw.put(0x44, KEY_F10);
        lwjglToGlfw.put(0x45, KEY_NUMLOCK);
        lwjglToGlfw.put(0x46, KEY_SCROLLLOCK); /* Scroll Lock */
        lwjglToGlfw.put(0x47, KEY_NUMPAD7);
        lwjglToGlfw.put(0x48, KEY_NUMPAD8);
        lwjglToGlfw.put(0x49, KEY_NUMPAD9);
        lwjglToGlfw.put(0x4A, KEY_MINUS); /* - on numeric keypad */
        lwjglToGlfw.put(0x4B, KEY_NUMPAD4);
        lwjglToGlfw.put(0x4C, KEY_NUMPAD5);
        lwjglToGlfw.put(0x4D, KEY_NUMPAD6);
        lwjglToGlfw.put(0x4E, KEY_ADD); /* + on numeric keypad */
        lwjglToGlfw.put(0x4F, KEY_NUMPAD1);
        lwjglToGlfw.put(0x50, KEY_NUMPAD2);
        lwjglToGlfw.put(0x51, KEY_NUMPAD3);
        lwjglToGlfw.put(0x52, KEY_NUMPAD0);
        lwjglToGlfw.put(0x53, KEY_NUMPADCOMMA); /* . on numeric keypad */
        lwjglToGlfw.put(0x57, KEY_F11);
        lwjglToGlfw.put(0x58, KEY_F12);
        lwjglToGlfw.put(0x64, KEY_F13); /*                     (NEC PC98) */
        lwjglToGlfw.put(0x65, KEY_F14); /*                     (NEC PC98) */
        lwjglToGlfw.put(0x66, KEY_F15); /*                     (NEC PC98) */
        lwjglToGlfw.put(0x67, KEY_F16); /* Extended Function keys - (Mac) */
        lwjglToGlfw.put(0x68, KEY_F17);
        lwjglToGlfw.put(0x69, KEY_F18);
        /*lwjglToGlfw.put(0x70, KEY_KANA);*/ /* (Japanese keyboard)            */
        lwjglToGlfw.put(0x71, KEY_F19); /* Extended Function keys - (Mac) */
        /*lwjglToGlfw.put(0x79, KEY_CONVERT);*/ /* (Japanese keyboard)            */
        /*lwjglToGlfw.put(0x7B, KEY_NOCONVERT);*/ /* (Japanese keyboard)            */
        /*lwjglToGlfw.put(0x7D, KEY_YEN);*/ /* (Japanese keyboard)            */
        lwjglToGlfw.put(0x8D, KEY_NUMPADEQUALS); /* = on numeric keypad (NEC PC98) */
        /*lwjglToGlfw.put(0x90, KEY_CIRCUMFLEX);*/ /* (Japanese keyboard)            */
        /*lwjglToGlfw.put(0x91, KEY_AT);*/ /*                     (NEC PC98) */
        /*lwjglToGlfw.put(0x92, KEY_COLON);*/ /*                     (NEC PC98) */
        /*lwjglToGlfw.put(0x93, KEY_UNDERLINE);*/ /*                     (NEC PC98) */
        /*lwjglToGlfw.put(0x94, KEY_KANJI);*/ /* (Japanese keyboard)            */
        /*lwjglToGlfw.put(0x95, KEY_STOP);*/ /*                     (NEC PC98) */
        /*lwjglToGlfw.put(0x96, KEY_AX);*/ /*                     (Japan AX) */
        /*lwjglToGlfw.put(0x97, KEY_UNLABELED);*/ /*                        (J3100) */
        lwjglToGlfw.put(0x9C, KEY_NUMPADENTER); /* Enter on numeric keypad */
        lwjglToGlfw.put(0x9D, KEY_RCONTROL);
        /*lwjglToGlfw.put(0xA7, KEY_SECTION);*/ /* Section symbol (Mac) */
        lwjglToGlfw.put(0xB3, KEY_NUMPADCOMMA); /* , on numeric keypad (NEC PC98) */
        lwjglToGlfw.put(0xB5, KEY_SLASH); /* / on numeric keypad */
        /* lwjglToGlfw.put(0xB7, KEY_SYSRQ);*/
        lwjglToGlfw.put(0xB8, KEY_RALT); /* right Alt */
        /*lwjglToGlfw.put(0xC4, KEY_FUNCTION);*/ /* Function (Mac) */
        lwjglToGlfw.put(0xC5, KEY_PAUSE); /* Pause */
        lwjglToGlfw.put(0xC7, KEY_HOME); /* Home on arrow keypad */
        lwjglToGlfw.put(0xC8, KEY_UP); /* UpArrow on arrow keypad */
        lwjglToGlfw.put(0xC9, KEY_PAGEUP); /* PgUp on arrow keypad */
        lwjglToGlfw.put(0xCB, KEY_LEFT); /* LeftArrow on arrow keypad */
        lwjglToGlfw.put(0xCD, KEY_RIGHT); /* RightArrow on arrow keypad */
        lwjglToGlfw.put(0xCF, KEY_END); /* End on arrow keypad */
        lwjglToGlfw.put(0xD0, KEY_DOWN); /* DownArrow on arrow keypad */
        lwjglToGlfw.put(0xD1, KEY_PAGEDOWN); /* PgDn on arrow keypad */
        lwjglToGlfw.put(0xD2, KEY_INSERT); /* Insert on arrow keypad */
        lwjglToGlfw.put(0xD3, KEY_DELETE); /* Delete on arrow keypad */
        /*lwjglToGlfw.put(0xDA, KEY_CLEAR);*/ /* Clear key (Mac) */
        lwjglToGlfw.put(0xDB, KEY_LWIN); /* Left Windows/Option key */

        lwjglToGlfw.put(0xDC, KEY_RWIN); /* Right Windows/Option key */

        /*lwjglToGlfw.put(0xDD, KEY_APPS);*/ /* AppMenu key */
        /*lwjglToGlfw.put(0xDE, KEY_POWER);*/
        /*lwjglToGlfw.put(0xDF, KEY_SLEEP);*/

        LWJGL_TO_GLFW = Collections.unmodifiableMap(lwjglToGlfw);
    }

    private static final Map<Integer, Integer> GLFW_TO_LWJGL;

    static {
        Map<Integer, Integer> glfwToLwjgl = new HashMap<>();
        LWJGL_TO_GLFW.forEach((lwjgl, glfw) -> glfwToLwjgl.put(glfw, lwjgl));

        GLFW_TO_LWJGL = Collections.unmodifiableMap(glfwToLwjgl);
    }


    public static boolean isHoldingShift(int modifiers) {
        return (modifiers & 1) == 1;
    }

    public static int lwjglToGlfw(int lwjglKey) {
        Integer glfw = LWJGL_TO_GLFW.get(lwjglKey);
        return glfw == null ? -1 : glfw;
    }

    public static int glfwToLwjgl(int glfwKey) {
        Integer lwjgl = GLFW_TO_LWJGL.get(glfwKey);
        return lwjgl == null ? 0 : lwjgl;
    }
}
