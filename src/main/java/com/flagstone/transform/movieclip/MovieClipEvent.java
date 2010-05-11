/*
 * MovieClipEvent.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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

    /**
     * Table used to store instances of Basic Actions so only one object is
     * created for each type of action decoded.
     */
    private static final Map<Integer, MovieClipEvent> TABLE
            = new LinkedHashMap<Integer, MovieClipEvent>();

    static {
        for (final MovieClipEvent event : values()) {
            TABLE.put(event.value, event);
        }
    }

    /**
     * Returns the MovieClipEvent for a given event.
     *
     * @param event
     *            the event that identifies the action when it is encoded.
     *
     * @return a shared instance of the object representing a given event.
     */
    public static MovieClipEvent fromInt(final int event) {
        return TABLE.get(event);
    }

    /** Encoded MovieClipEvent value. */
    private final transient int value;

    /**
     * Creates a MovieClipEvent object with the specified value.
     *
     * @param eval
     *            the value for the event.
     */
    private MovieClipEvent(final int eval) {
        value = eval;
    }

    /**
     * Get value used to encode the MovieClipEvent.
     *
     * @return the encoded event.
     */
    public int getValue() {
        return value;
    }
}
