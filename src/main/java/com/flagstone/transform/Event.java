/*
 * Event.java Transform Copyright (c) 2010 Flagstone Software Ltd. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of Flagstone Software Ltd. nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission. THIS SOFTWARE
 * IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform;

/**
 * Event is used to represent all the different types of event that a button or
 * movie clips responds to.
 */
public enum Event {

    // Keep the order for ROLL_OUT and ROLL_OVER. A set of Events are used for
    // encoding ButtonSounds and ROLL_OUT must be before ROLL_OVER so the
    // correct natural order is used when creating an EnumSet.

    /**
     * The ROLL_OUT event occurs when the mouse cursor moves out of the active
     * area of a button or movie clip.
     */
    ROLL_OUT,
    /**
     * The ROLL_OVER event that occurs when the mouse cursor moves over the
     * active area of a button or movie clip.
     */
    ROLL_OVER,
    /**
     * The PRESS event occurs when the mouse button is clicked while the mouse
     * cursor is over the active area of the button or movie clip.
     */
    PRESS,
    /**
     * The RELEASE event occurs when the mouse button is clicked and released
     * while the mouse cursor is over the active area of the button or movie
     * clip.
     */
    RELEASE,
    /**
     * The DRAG_OUT event occurs when the mouse button is clicked and the mouse
     * cursor is dragged out of the active area of the button or movie clip.
     */
    DRAG_OUT,
    /**
     * The DRAG_OVER event occurs when the mouse button is clicked and the mouse
     * cursor is dragged into the active area of the button.
     */
    DRAG_OVER,
    /**
     * The RELEASE_OUT event occurs when the mouse button is clicked, the mouse
     * cursor is dragged into the active area of the button or movie clip and
     * the mouse button is released.
     */
    RELEASE_OUT,
    /**
     * The LOAD event occurs when a movie clip has finished loading.
     */
    LOAD,
    /**
     * The ENTER_FRAME event occurs when the mouse enters the screen.
     */
    ENTER_FRAME,
    /**
     * The UNLOAD event occurs when a movie clip has been unloaded.
     */
    UNLOAD,
    /**
     * The MOUSE_MOVE event occurs when the mouse is moved.
     */
    MOUSE_MOVE,
    /**
     * The MOUSE_DOWN event occurs when the mouse left-button is clicked. Movie
     * clips only.
     */
    MOUSE_DOWN,
    /**
     * The MOUSE_UP event occurs when the mouse left-button is released. Movie
     * clips only.
     */
    MOUSE_UP,
    /**
     * The KEY_DOWN event occurs when a key is pressed on the keyboard. Movie
     * clips only.
     */
    KEY_DOWN,
    /**
     * The KEY_UP event occurs when a key is released on the keyboard. Movie
     * clips only.
     */
    KEY_UP,
    /**
     * The DATA event occurs when a movie clip when the results of a GetUrl2
     * action have been loaded with the movie clip specified as the target.
     */
    DATA,
    /**
     * The INITIALIZE event occurs when a movie clip has finished initializing.
     */
    INITIALIZE,
    /**
     * The KEY_PRESS event occurs when the specified key on the keyboard is
     * pressed. Movie clips only.
     */
    KEY_PRESS,
    /**
     * The CONSTRUCT event occurs when a movie clip is dynamically created
     * using actionscript.
     */
    CONSTRUCT;
}
