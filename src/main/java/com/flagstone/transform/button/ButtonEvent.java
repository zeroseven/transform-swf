/*
 * ButtonEvent.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

/** TODO(class). */
public enum ButtonEvent {
    
    // Keep the order for ROLL_OUT and ROLL_OVER though the event codes are
    // not in the right order. A set of ButtonEvents are used for encoding 
    // ButtonSounds and ROLL_OUT must be before ROLL_OVER so the correct 
    // natural order is used when creating an EnumSet,
    
    /**
     * Code for the button event that occurs when the mouse cursor moves out of
     * the active area of a button.
     */
    ROLL_OUT(2),
    /**
     * Code for the button event that occurs when the mouse cursor moves over
     * the active area of a button.
     */
    ROLL_OVER(1),
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
