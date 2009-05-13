/*
 * Property.java
 * Transform
 *
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform.action;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Property defines the set of attributes that can accessed for movies and movie
 * clips when executing actions.
 *
 * @see Push
 */
public enum Property {
    /**
     * The x-origin of the movie clip relative to the parent clip. This is
     * equivalent to the _x property in actionscript.
     */
    X(0),
    /**
     * The y-origin of the movie clip relative to the parent clip. This is
     * equivalent to the _y property in actionscript.
     */
    Y(1),
    /**
     * The scaling factor of the movie clip in the x direction. This is
     * equivalent to the _xscale property in actionscript.
     */
    XSCALE(2),
    /**
     * The scaling factor of the movie clip in the x direction. This is
     * equivalent to the _yscale property in actionscript.
     */
    YSCALE(3),
    /**
     * The number of the current frame playing in the movie clip. This is
     * equivalent to the _currentframe property in actionscript.
     */
    CURRENT_FRAME(4),
    /**
     * The total number of frames in the movie clip. This is equivalent to the
     * _totalframes property in actionscript.
     */
    TOTAL_FRAMES(5),
    /**
     * The transparency of the movie clip. This is equivalent to the _alpha
     * property in actionscript.
     */
    ALPHA(6),
    /**
     * Whether the movie clip is visible. This is equivalent to the _visible
     * property in actionscript.
     */
    VISIBLE(7),
    /**
     * The width of the movie clip in pixels. This is equivalent to the _width
     * property in actionscript.
     */
    WIDTH(8),
    /**
     * The height of the movie clip in pixels. This is equivalent to the _height
     * property in actionscript.
     */
    HEIGHT(9),
    /**
     * The angle of rotation of the movie clip in degrees. This is equivalent to
     * the _height property in actionscript.
     */
    ROTATION(10),
    /**
     * The path of the movie clip relative to the root movie in the Player. This
     * is equivalent to the _rotation property in actionscript.
     */
    TARGET(11),
    /**
     * The number of frames form the movie clip loaded. This is equivalent to
     * the _framesloaded property in actionscript.
     */
    FRAMES_LOADED(12),
    /**
     * The name of movie clip. This is equivalent to the _name property in
     * actionscript.
     */
    NAME(13),
    /**
     * The name of the movie clip currently being dragged. This is equivalent to
     * the _target property in actionscript.
     */
    DROP_TARGET(14),
    /**
     * The URL from which the movie clip was loaded. This is equivalent to the
     * _url property in actionscript.
     */
    URL(15),
    /**
     * Identifies the level of aliasing being performed by the Player. This is
     * equivalent to the _highquality property in actionscript.
     */
    HIGH_QUALITY(16),
    /**
     * Identifies whether a rectangle is drawn around a button or text field
     * that has the current focus This is equivalent to the _focusrect property
     * in actionscript. .
     */
    FOCUS_RECT(17),
    /**
     * The amount of time streaming sound is buffered by the Player before
     * playing. This is equivalent to the _soundbuftime property in
     * actionscript.
     */
    SOUND_BUF_TIME(18),
    /**
     * Identifies the level of rendering quality being performed by the Player.
     * This is equivalent to the _quality property in actionscript.
     */
    QUALITY(19),
    /**
     * The current x-coordinate of the mouse pointer on the Player screen. This
     * is equivalent to the _xmouse property in actionscript.
     */
    XMOUSE(20),
    /**
     * The current y-coordinate of the mouse pointer on the Player screen. This
     * is equivalent to the _ymouse property in actionscript.
     */
    YMOUSE(21);

    private static final Map<Integer, Property> TABLE;

    static {
        TABLE = new LinkedHashMap<Integer, Property>();

        for (final Property property : values()) {
            TABLE.put(property.value, property);
            TABLE.put(Float.floatToIntBits(property.value), property);
        }
    }

    /**
     * Get the Property that is identified by an integer value. This method is
     * used when decoding a Property from a Flash file.
     *
     * @param type
     *            the integer value read from a Flash file.
     *
     * @return the Property identified by the integer value.
     */
    public static Property fromInt(final int type) {
        return TABLE.get(type);
    }

    private final int value;

    private Property(final int value) {
        this.value = value;
    }

    /**
     * Get the integer value that is used to identify this Property. This method is
     * used when encoding a Property in a Flash file.
     *
     * @return the integer value used to encode this Property.
     */
    public int getValue(final int version) {
        return version < 5 ? Float.floatToIntBits(value) : value;
    }
}
