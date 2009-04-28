package com.flagstone.transform.button;

public enum ButtonState {
    /** The button is up. */
    UP(1),
    /** The mouse is over the active area of the button. */
    OVER(2),
    /** The button is being clicked. */
    DOWN(4),
    /** The active area of the button. */
    ACTIVE(8);

    private final int value;

    private ButtonState(final int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }
}
