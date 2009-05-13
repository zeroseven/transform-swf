/*
 * Background.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.datatype.Color;

/**
 * Background sets the background colour displayed in every frame in the movie.
 *
 * <p>
 * Although the colour is specified using an Color object the colour displayed
 * is completely opaque - the alpha channel information in the object is
 * ignored.
 * </p>
 *
 * <p>
 * The background colour must be set before the first frame is displayed
 * otherwise the background colour defaults to white. This is typically the
 * first object in a coder. If more than one Background object is added to a
 * movie then only first one sets the background colour. Subsequent objects are
 * ignored.
 * </p>
 *
 * @see Color
 */
public final class Background implements MovieTag {

    private static final String FORMAT = "Background: { color=%s }";

    private Color color;

    /**
     * Creates and initialises a Background object using values encoded in the
     * Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public Background(final SWFDecoder coder, final Context context)
            throws CoderException {
        if ((coder.readWord(2, false) & SWFDecoder.MASK_LENGTH) > SWFDecoder.MAX_LENGTH) {
            coder.readWord(4, false);
        }
        color = new Color(coder, context);
    }

    /**
     * Creates a Background object with a the specified colour.
     *
     * @param aColor
     *            the colour for the background. Must not be null.
     */
    public Background(final Color aColor) {
        setColor(aColor);
    }

    /**
     * Creates and initialises an Background object using the values
     * copied from another Background object.
     *
     * @param object
     *            a Background object from which the values will be
     *            copied.
     */
    public Background(final Background object) {
        color = object.color;
    }

    /**
     * Returns the colour for the movie background.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the colour for the movie background.
     *
     * @param color
     *            the colour for the background. Must not be null.
     */
    public void setColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException(Strings.OBJECT_IS_NULL);
        }
        this.color = color;
    }

    /** {@inheritDoc} */
    public Background copy() {
        return new Background(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, color.toString());
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 2 + Color.RGB;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeWord((MovieTypes.SET_BACKGROUND_COLOR << SWFEncoder.LENGTH_BITS)
                | Color.RGB, 2);
        color.encode(coder, context);
    }
}
