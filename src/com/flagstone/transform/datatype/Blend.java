/*
 * Blend.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.datatype;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Blend modes let you control how the colours and transparency of successive
 * layers are composited together when the Flash Player displays the objects on
 * the screen. The effect is to create highlights, shadows or to control how an
 * underlying object appears.
 */
public enum Blend {
    /**
     * Applies colour form the current layer normally with no blending with the
     * underlying layers.
     */
    NORMAL(1),
    /** Sets the opacity of the current layer at 100% before blending. */
    LAYER(2),
    /** Multiplies layers together. This has the effect of darkening the layer. */
    MULTIPLY(3),
    /**
     * Multiplies this inverse of the layer with the underlying layer, creating
     * a bleaching effect.
     */
    SCREEN(4),
    /**
     * Displays colours from the underlying layer that are lighter than the
     * current layer.
     */
    LIGHTEN(5),
    /**
     * Displays colours from the underlying layer that are darker than the
     * current layer.
     */
    DARKEN(6),
    /** Add the colours of the layers together. */
    ADD(7),
    /** Subtract the current layer colour from the underlying layer. */
    SUBTRACT(8),
    /**
     * Subtracts the largest colour value from the smallest, creating a colour
     * negative effect.
     */
    DIFFERENCE(9),
    /** Inverts the colours of the current layer. */
    INVERT(10),
    /** Applies the transparency of the current layer to the underlying layer. */
    ALPHA(11),
    /**
     * Delete the colours from the underlying layer that match the colour on the
     * current layer.
     */
    ERASE(12),
    /**
     * Use the colour from the current layer to select colours from the
     * underlying layer.
     */
    OVERLAY(13),
    /**
     * Select colours from the underlying layer using the values on the current
     * layer.
     */
    HARDLIGHT(14);

    private static final Map<Integer, Blend> TABLE = new LinkedHashMap<Integer, Blend>();

    static {
        for (final Blend format : values()) {
            TABLE.put(format.value, format);
        }
    }

    public static Blend fromInt(final int type) {
        return TABLE.get(type);
    }

    private final int value;

    private Blend(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
