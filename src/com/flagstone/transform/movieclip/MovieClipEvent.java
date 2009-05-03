package com.flagstone.transform.movieclip;

import java.util.LinkedHashMap;
import java.util.Map;

/** TODO(class). */
public enum MovieClipEvent {
    /** Code for a load event. */
    LOAD(1),

    /** Code for an enter frame event. */
    ENTER_FRAME(2),

    /** Code for an unload event. */
    UNLOAD(4),

    /** Code for a mouse move event. */
    MOUSE_MOVE(8),

    /** Code for a mouse down event. */
    MOUSE_DOWN(16),

    /** Code for a mouse up event. */
    MOUSE_UP(32),

    /** Code for a key down event. */
    KEY_DOWN(64),

    /** Code for a key up event. */
    KEY_UP(128),

    /** Code for a data event. */
    DATA(256),

    /** Code for an initialise event. */
    INITIALIZE(512),

    /** Code for a press event. */
    PRESS(1024),

    /** Code for a release event. */
    RELEASE(2048),

    /** Code for a release outside event. */
    RELEASE_OUT(4096),

    /** Code for a roll over event. */
    ROLL_OVER(8192),

    /** Code for a roll out event. */
    ROLL_OUT(16384),

    /** Code for a drag over event. */
    DRAG_OVER(32768),

    /** Code for a drag out event. */
    DRAG_OUT(65536),

    /** Code for a key press event, where the code for the key is specified. */
    KEY_PRESS(131072),

    /** Code for a construct event. */
    CONSTRUCT(262144);

    private static final Map<Integer, MovieClipEvent> TABLE = new LinkedHashMap<Integer, MovieClipEvent>();

    static {
        for (final MovieClipEvent event : values()) {
            TABLE.put(event.value, event);
        }
    }

    /** TODO(method). */
    public static MovieClipEvent fromInt(final int type) {
        return TABLE.get(type);
    }

    private final int value;

    private MovieClipEvent(final int value) {
        this.value = value;
    }

    /** TODO(method). */
    public int getValue() {
        return value;
    }
}
