/*
 * Spread.java
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
package com.flagstone.transform.fillstyle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Spread describes how the gradient is used to fill the available shape
 * when the area to be filled is larger than the area covered by the gradient.
 */
public enum Spread {
    /** The last colour of the gradient is used to fill the remaining area. */
    PAD(0),
    /** The gradient is reflected (repeatedly reversing the gradient) across
     * the area to be filled.
     */
    REFLECT(0x40),
    /** The gradient is repeated across the area to be filled. */
    REPEAT(0xC0);

    /**
     * Table used to store instances of Spreads so only one object is
     * created for each type decoded.
     */
    private static final Map<Integer, Spread> TABLE
            = new LinkedHashMap<Integer, Spread>();

    static {
        for (final Spread action : values()) {
            TABLE.put(action.value, action);
        }
    }

    /**
     * Returns the Spread for a given type.
     *
     * @param value
     *            the type that identifies the Spread when it is encoded.
     *
     * @return a shared instance of the object representing a given Spread type.
     */
    public static Spread fromInt(final int value) {
        return TABLE.get(value);
    }

    /** Type used to identify the Spread when it is encoded. */
    private final int value;

    /**
     * Constructor used to create instances for each type of Spread.
     *
     * @param spread the value representing the Spread when it is encoded.
     */
    private Spread(final int spread) {
        value = spread;
    }

    /**
     * Get the value used to represent the Spread when it is encoded.
     * @return the encoded value for the Spread.
     */
    public int getValue() {
        return value;
    }
}
