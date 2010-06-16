/*
 * Glyph.java
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

package com.flagstone.transform.util.font;

import java.util.Arrays;

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.shape.Shape;

/**
 * TrueTypeGlyph is a simple container class used to decode TrueType glyphs.
 */
public final class TrueTypeGlyph extends Glyph {

    private transient int[] xCoordinates = new int[]{};
    private transient int[] yCoordinates = new int[]{};
    private transient boolean[] onCurve = new boolean[]{};
    private transient int[] endPoints = new int[]{};

    /**
     * Create a TrueTypeGlyph with the specified outline, bounding box and
     * advance.
     * @param aShape the outline of the glyph.
     * @param rect the bounding box that encloses the glyph.
     * @param dist the advance to the next glyph.
     */
    public TrueTypeGlyph(final Shape aShape, final Bounds rect,
            final int dist) {
        super(aShape, rect, dist);
    }

    /**
     * Create a TrueTypeGlyph with the specified outline - the bounding box
     * and advance default to zero.
     * @param aShape the outline of the glyph.
     */
    public TrueTypeGlyph(final Shape aShape) {
        super(aShape);
    }

    public void getXCoordinates(final int[] array) {
        final int count = xCoordinates.length;
        for (int i = 0; i < count; i++) {
            array[i] = xCoordinates[i];
        }
    }

    public void getYCoordinates(final int[] array) {
        final int count = yCoordinates.length;
        for (int i = 0; i < count; i++) {
            array[i] = yCoordinates[i];
        }
    }

    public void getEnd(final int[] array) {
        final int count = endPoints.length;
        for (int i = 0; i < count; i++) {
            array[i] = endPoints[i];
        }
    }

    public void getCurve(final boolean[] array) {
        final int count = onCurve.length;
        for (int i = 0; i < count; i++) {
            array[i] = onCurve[i];
        }
    }

    public void setCoordinates(final int[] xcoords, final int[] ycoords) {
        xCoordinates = Arrays.copyOf(xcoords, xcoords.length);
        yCoordinates = Arrays.copyOf(ycoords, ycoords.length);
    }

    public void setOnCurve(final boolean[] array) {
        onCurve = Arrays.copyOf(array, array.length);
    }

    public void setEnds(final int[] array) {
        endPoints = Arrays.copyOf(array, array.length);
    }

    public int numberOfPoints() {
        return xCoordinates.length;
    }

    public int numberOfContours() {
        return endPoints.length;
    }
}
