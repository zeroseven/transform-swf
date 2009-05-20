package com.flagstone.transform.button;

import java.util.LinkedHashMap;
import java.util.Map;

/** TODO(class). */
public enum ButtonEvent {
    /**
     * Code for the button event that occurs when the mouse cursor moves over
     * the active area of a button.
     */
    ROLL_OVER(1),
    /**
     * Code for the button event that occurs when the mouse cursor moves out of
     * the active area of a button.
     */
    ROLL_OUT(2),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * while the mouse cursor is over the active area of the button.
     */
    PRESS(4),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and released while the mouse cursor is over the active area of the
     * button.
     */
    RELEASE(8),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged out of the active area of the button.
     */
    DRAG_OUT(16),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged into the active area of the button.
     */
    DRAG_OVER(32),
    /**
     * Code for the button event that occurs when the mouse button is clicked,
     * the mouse cursor is dragged into the active area of the button and the
     * mouse button is released.
     */
    RELEASE_OUT(64),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged into the active area of the menu item.
     */
    MENU_DRAG_OVER(128),
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged out of the active area of the menu item.
     */
    MENU_DRAG_OUT(256);

    private static final Map<Integer, ButtonEvent> TABLE = new LinkedHashMap<Integer, ButtonEvent>();

    static {
        for (final ButtonEvent type : values()) {
            TABLE.put(type.value, type);
        }
    }

    protected static ButtonEvent fromInt(final int type) {
        return TABLE.get(type);
    }

    private final int value;

    private ButtonEvent(final int value) {
        this.value = value;
    }

    protected int getValue() {
        return value;
    }
}
