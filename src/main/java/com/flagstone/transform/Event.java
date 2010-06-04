/*
 * Event.java
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

package com.flagstone.transform;

/**
 * Event is used to represent all the different types of event that a
 * button or movie clips responds to.
 */
public enum Event {

    // Keep the order for ROLL_OUT and ROLL_OVER. A set of Events are used for
    // encoding ButtonSounds and ROLL_OUT must be before ROLL_OVER so the
    // correct natural order is used when creating an EnumSet.

    /**
     * Code for the button event that occurs when the mouse cursor moves out of
     * the active area of a button.
     */
    ROLL_OUT,
    /**
     * Code for the button event that occurs when the mouse cursor moves over
     * the active area of a button.
     */
    ROLL_OVER,
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * while the mouse cursor is over the active area of the button.
     */
    PRESS,
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and released while the mouse cursor is over the active area of the
     * button.
     */
    RELEASE,
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged out of the active area of the button.
     */
    DRAG_OUT,
    /**
     * Code for the button event that occurs when the mouse button is clicked
     * and the mouse cursor is dragged into the active area of the button.
     */
    DRAG_OVER,
    /**
     * Code for the button event that occurs when the mouse button is clicked,
     * the mouse cursor is dragged into the active area of the button and the
     * mouse button is released.
     */
    RELEASE_OUT,
    /** Code for a load event. */
    LOAD,
    /** Code for an enter frame event. */
    ENTER_FRAME,
    /** Code for an unload event. */
    UNLOAD,
    /** Code for a mouse move event. */
    MOUSE_MOVE,
    /** Code for a mouse down event. */
    MOUSE_DOWN,
    /** Code for a mouse up event. */
    MOUSE_UP,
    /** Code for a key down event. */
    KEY_DOWN,
    /** Code for a key up event. */
    KEY_UP,
    /** Code for a data event. */
    DATA,
    /** Code for an initialise event. */
    INITIALIZE,
    /** Code for a key press event, where the code for the key is specified. */
    KEY_PRESS,
    /** Code for a construct event. */
    CONSTRUCT;
}
