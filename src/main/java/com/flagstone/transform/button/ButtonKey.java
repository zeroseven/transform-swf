/*
 * ButtonKey.java
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

package com.flagstone.transform.button;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ButtonKey is used to provide mapping from special keys to codes recognised
 * by the Flash Player.
 */
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

    private ButtonKey(final int keyCode) {
        value = keyCode;
    }

    public int getValue() {
        return value;
    }
}
