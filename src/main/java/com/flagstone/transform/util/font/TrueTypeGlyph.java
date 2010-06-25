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

    /** The set of x coordinates representing a segment of a glyph. */
    private transient int[] xCoordinates = new int[]{};
    /** The set of y coordinates representing a segment of a glyph. */
    private transient int[] yCoordinates = new int[]{};
    /** Flags indicating which point in on the segment. */
    private transient boolean[] onCurve = new boolean[]{};
    /** The set of end points for each point on the segment. */
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

    /**
     * Get the set of x coordinates representing a segment of a glyph.
     * @param array an array where the points will be stored.
     */
    public void getXCoordinates(final int[] array) {
        System.arraycopy(xCoordinates, 0, array, 0, xCoordinates.length);
    }

    /**
     * Get the set of y coordinates representing a segment of a glyph.
     * @param array an array where the points will be stored.
     */
    public void getYCoordinates(final int[] array) {
        System.arraycopy(yCoordinates, 0, array, 0, yCoordinates.length);
    }

    /**
     * Get the set of end points for the segment of a glyph.
     * @param array an array where the points will be stored.
     */
    public void getEnd(final int[] array) {
        System.arraycopy(endPoints, 0, array, 0, endPoints.length);
    }

    /**
     * Get the set of flags which indicate which point are on the segment.
     * @param array an array where the flags will be stored.
     */
    public void getCurve(final boolean[] array) {
        System.arraycopy(onCurve, 0, array, 0, onCurve.length);
    }

    /**
     * Set the points for a segment of a glyph.
     * @param xcoords the set of x-coordinates for the points.
     * @param ycoords the set of y-coordinates for the points.
     */
    public void setCoordinates(final int[] xcoords, final int[] ycoords) {
        xCoordinates = Arrays.copyOf(xcoords, xcoords.length);
        yCoordinates = Arrays.copyOf(ycoords, ycoords.length);
    }

    /**
     * Indicate which points are on the segment.
     * @param array an array where the flags will be stored.
     */
    public void setOnCurve(final boolean[] array) {
        onCurve = Arrays.copyOf(array, array.length);
    }

    /**
     * Set the end-points for a segment of a glyph.
     * @param array the set of end points.
     */
    public void setEnds(final int[] array) {
        endPoints = Arrays.copyOf(array, array.length);
    }

    /**
     * Get the number of points in the segment.
     * @return the number of points.
     */
    public int numberOfPoints() {
        return xCoordinates.length;
    }

    /**
     * Get the number of contours.
     * @return the number of contours.
     */
    public int numberOfContours() {
        return endPoints.length;
    }
}
