/*
 * Curve.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.shape;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.Encoder;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * <p>
 * Curve is used to define a curve. Curved lines are constructed using a
 * Quadratic Bezier curve. The curve is specified using two points, an off-curve
 * control point, relative to the current point and an on-curve anchor point
 * which defines the end-point of the curve, and which is specified relative to
 * the anchor point.
 * </p>
 * 
 * <img src="doc-files/quadratic.gif">
 * 
 * <p>
 * Flash does not directly support Cubic Bezier curves. Converting a Cubic
 * Bezier curve to a Quadratic curve is a non trivial process, however the
 * Canvas class contains a method to perform the conversion simplifying the
 * create of Shape outlines in Flash from other graphics formats.
 * </p>
 * 
 * @see com.flagstone.transform.util.shape.Canvas
 */
public final class Curve implements ShapeRecord {
    private static final String FORMAT = "Curve: control=(%d,%d), anchor=(%d,%d);";

    private transient int controlX;
    private transient int controlY;
    private transient int anchorX;
    private transient int anchorY;

    private transient int size;

    // TODO(doc)
    public Curve(final SWFDecoder coder) throws CoderException {
        // skip over shapeType and edgeType
        coder.adjustPointer(2);
        size = coder.readBits(4, false) + 2;
        controlX = coder.readBits(size, true);
        controlY = coder.readBits(size, true);
        anchorX = coder.readBits(size, true);
        anchorY = coder.readBits(size, true);
    }

    /**
     * Creates a Curve object specifying the anchor and control point
     * coordinates.
     * 
     * @param controlX
     *            the x-coordinate of the control point, specified relative to
     *            the current drawing point. Must be in the range -65535..65535.
     * @param controlY
     *            the y-coordinate of the control point, specified relative to
     *            the current drawing point.Must be in the range -65535..65535.
     * @param anchorX
     *            the x-coordinate of the anchor point, specified relative to
     *            the control point.Must be in the range -65535..65535.
     * @param anchorY
     *            the y-coordinate of the anchor point, specified relative to
     *            the control point.Must be in the range -65535..65535.
     */
    public Curve(final int controlX, final int controlY, final int anchorX,
            final int anchorY) {
        setPoints(controlX, controlY, anchorX, anchorY);
    }

    // TODO(doc)
    public Curve(final Curve object) {
        controlX = object.controlX;
        controlY = object.controlY;
        anchorX = object.anchorX;
        anchorY = object.anchorY;
    }

    /**
     * Returns the x-coordinate of the control point relative to the current
     * drawing point.
     */
    public int getControlX() {
        return controlX;
    }

    /**
     * Returns the y-coordinate of the control point relative to the current
     * drawing point.
     */
    public int getControlY() {
        return controlY;
    }

    /**
     * Returns the x-coordinate of the anchor point relative to the control
     * point.
     */
    public int getAnchorX() {
        return anchorX;
    }

    /**
     * Returns the y-coordinate of the anchor point relative to the control
     * point.
     */
    public int getAnchorY() {
        return anchorY;
    }

    /**
     * Sets the x and y coordinates of the control and anchor points. Values
     * must be in the range -65535..65535.
     * 
     * @param controlX
     *            the x-coordinate of the control point.
     * @param controlY
     *            the y-coordinate of the control point.
     * @param anchorX
     *            the x-coordinate of the anchor point.
     * @param anchorY
     *            the y-coordinate of the anchor point.
     */
    public void setPoints(final int controlX, final int controlY,
            final int anchorX, final int anchorY) {
        if ((controlX < -65535) || (controlX > 65535)) {
            throw new IllegalArgumentException(Strings.COORDINATES_RANGE);
        }
        this.controlX = controlX;

        if ((controlY < -65535) || (controlY > 65535)) {
            throw new IllegalArgumentException(Strings.COORDINATES_RANGE);
        }
        this.controlY = controlY;

        if ((anchorX < -65535) || (anchorX > 65535)) {
            throw new IllegalArgumentException(Strings.COORDINATES_RANGE);
        }
        this.anchorX = anchorX;

        if ((anchorY < -65535) || (anchorY > 65535)) {
            throw new IllegalArgumentException(Strings.COORDINATES_RANGE);
        }
        this.anchorY = anchorY;
    }

    public Curve copy() {
        return new Curve(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, controlX, controlY, anchorX, anchorY);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        int numberOfBits = 6;

        size = Encoder.maxSize(controlX, controlY, anchorX, anchorY, 1);

        numberOfBits += size << 2;

        context.getVariables().put(Context.SHAPE_SIZE,
                context.getVariables().get(Context.SHAPE_SIZE) + numberOfBits);

        return numberOfBits;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeBits(2, 2); // shapeType, edgeType
        coder.writeBits(size - 2, 4);
        coder.writeBits(controlX, size);
        coder.writeBits(controlY, size);
        coder.writeBits(anchorX, size);
        coder.writeBits(anchorY, size);
    }
}
