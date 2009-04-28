/*
 * MorphSolidLine.java
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

package com.flagstone.transform.linestyle;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;
import com.flagstone.transform.shape.DefineMorphShape;

/**
 * MorphSolidLine defines the width and colour of a line drawn for a shape is it
 * is morphed.
 * 
 * <p>
 * MorphSolidLine specifies the width and colour of the line at the start and
 * end of the morphing process. The transparency value for the colour should
 * also be specified. The Flash Player performs the interpolation as the shape
 * is morphed.
 * </p>
 * 
 * @see DefineMorphShape
 */
public final class MorphLineStyle implements SWFEncodeable {
    private static final String FORMAT = "MorphSolidLine: { startWidth=%d; endWidth=%d; startColor=%s; endColor=%s }";

    private int startWidth;
    private int endWidth;
    private Color startColor;
    private Color endColor;

    // TODO(doc)
    public MorphLineStyle(final SWFDecoder coder, final Context context)
            throws CoderException {
        startWidth = coder.readWord(2, false);
        endWidth = coder.readWord(2, false);
        startColor = new Color(coder, context);
        endColor = new Color(coder, context);
    }

    /**
     * Creates a MorphLineStyle object specifying the starting and ending widths
     * and colours.
     * 
     * @param startWidth
     *            the width of the line at the start of the morphing process.
     * @param endWidth
     *            the width of the line at the end of the morphing process.
     * @param startColor
     *            the colour of the line at the start of the morphing process.
     * @param endColor
     *            the colour of the line at the end of the morphing process.
     */
    public MorphLineStyle(final int startWidth, final int endWidth,
            final Color startColor, final Color endColor) {
        super();

        setStartWidth(startWidth);
        setEndWidth(endWidth);
        setStartColor(startColor);
        setEndColor(endColor);
    }

    // TODO(doc)
    public MorphLineStyle(final MorphLineStyle object) {
        startWidth = object.startWidth;
        endWidth = object.endWidth;
        startColor = object.startColor;
        endColor = object.endColor;
    }

    /**
     * Returns the width of the line at the start of the morphing process.
     */
    public int getStartWidth() {
        return startWidth;
    }

    /**
     * Returns the width of the line at the end of the morphing process.
     */
    public int getEndWidth() {
        return endWidth;
    }

    /**
     * Returns the colour of the line at the start of the morphing process.
     */
    public Color getStartColor() {
        return startColor;
    }

    /**
     * Returns the colour of the line at the end of the morphing process.
     */
    public Color getEndColor() {
        return endColor;
    }

    /**
     * Sets the width of the line at the start of the morphing process.
     * 
     * @param aNumber
     *            the starting width of the line. Must be in the range 0..65535.
     */
    public void setStartWidth(final int aNumber) {
        if ((aNumber < 0) || (aNumber > 65535)) {
            throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
        }
        startWidth = aNumber;
    }

    /**
     * Sets the width of the line at the end of the morphing process.
     * 
     * @param aNumber
     *            the ending width of the line. Must be in the range 0..65535.
     */
    public void setEndWidth(final int aNumber) {
        if ((aNumber < 0) || (aNumber > 65535)) {
            throw new IllegalArgumentException(Strings.UNSIGNED_RANGE);
        }
        endWidth = aNumber;
    }

    /**
     * Returns the colour of the line at the start of the morphing process.
     * 
     * @param aColor
     *            the starting colour of the line. Must not be null.
     */
    public void setStartColor(final Color aColor) {
        if (aColor == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        startColor = aColor;
    }

    /**
     * Sets the colour of the line at the end of the morphing process.
     * 
     * @param aColor
     *            the ending colour of the line. Must not be null.
     */
    public void setEndColor(final Color aColor) {
        if (aColor == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        endColor = aColor;
    }

    public MorphLineStyle copy() {
        return new MorphLineStyle(this);
    }

    @Override
    public String toString() {
        return String
                .format(FORMAT, startWidth, endWidth, startColor, endColor);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 12;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeWord(startWidth, 2);
        coder.writeWord(endWidth, 2);
        startColor.encode(coder, context);
        endColor.encode(coder, context);
    }
}