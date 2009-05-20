package com.flagstone.transform.button;

import java.util.LinkedHashMap;
import java.util.Map;

/** TODO(class). */
public enum ButtonKey {
    /**
     * Code for the button event that occurs when the left arrow key is pressed
     * on the keyboard.
     */
    LEFT(1),
    /**
     * Code for the button event that occurs when the right arrow key is pressed
     * on the keyboard.
     */
    RIGHT(2),
    /**
     * Code for the button event that occurs when the home key is pressed on the
     * keyboard.
     */
    HOME(3),
    /**
     * Code for the button event that occurs when the end key is pressed on the
     * keyboard.
     */
    END(4),
    /**
     * Code for the button event that occurs when the insert key is pressed on
     * the keyboard.
     */
    INSERT(5),
    /**
     * Code for the button event that occurs when the delete key is pressed on
     * the keyboard.
     */
    DELETE(6),
    /**
     * Code for the button event that occurs when the backspace key is pressed
     * on the keyboard.
     */
    BACKSPACE(8),
    /**
     * Code for the button event that occurs when the enter key is pressed on
     * the keyboard.
     */
    ENTER(13),
    /**
     * Code for the button event that occurs when the up arrow key is pressed on
     * the keyboard.
     */
    UP(14),
    /**
     * Code for the button event that occurs when the down arrow key is pressed
     * on the keyboard.
     */
    DOWN(15),
    /**
     * Code for the button event that occurs when the page up key is pressed on
     * the keyboard.
     */
    PAGE_UP(16),
    /**
     * Code for the button event that occurs when the page down key is pressed
     * on the keyboard.
     */
    PAGE_DOWN(17),
    /**
     * Code for the button event that occurs when the tab key is pressed on the
     * keyboard.
     */
    TAB(18),
    /**
     * Code for the button event that occurs when the escape key is pressed on
     * the keyboard.
     */
    ESCAPE(19);

    private static final Map<Integer, ButtonKey> TABLE =
        new LinkedHashMap<Integer, ButtonKey>();

    static {
        for (final ButtonKey type : values()) {
            TABLE.put(type.value, type);
        }
    }

    public static ButtonKey fromInt(final int type) {
        return TABLE.get(type);
    }

    private final int value;

    private ButtonKey(final int value) {
        this.value = value;
    }

    public char getChar() {
        return (char)value;
    }
}
