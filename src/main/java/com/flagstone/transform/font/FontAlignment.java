/*
 * FontAlignment.java
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

package com.flagstone.transform.font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/** TODO(class). */
public final class FontAlignment implements MovieTag {

    /** TODO(class). */
    public enum StrokeWidth {
        /** TODO(doc). */
        THIN,
        /** TODO(doc). */
        MEDIUM,
        /** TODO(doc). */
        THICK
    };

    /** Format string used in toString() method. */
    private static final String FORMAT = "FontAlignment: { identifier=%d;"
    		+ " strokeWidth=%s; zones=%s }";

    /** The unique identifier of the font that the alignment applies to. */
    private int identifier;
    private transient int hints;
    private List<GlyphAlignment> zones;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a FontAlignment object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public FontAlignment(final SWFDecoder coder)
            throws IOException {
        final int start = coder.getPointer();
        length = coder.readLength();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();
        hints = coder.readByte();

        zones = new ArrayList<GlyphAlignment>();

        while (coder.getPointer() < end) {
            zones.add(new GlyphAlignment(coder));
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }


    public FontAlignment(final int uid, final StrokeWidth stroke,
            final List<GlyphAlignment> list) {
        setIdentifier(uid);
        setStrokeWidth(stroke);
        setZones(list);
    }

    /**
     * Creates and initialises a FontAlignment object using the values copied
     * from another FontAlignment object.
     *
     * @param object
     *            a FontAlignment object from which the values will be
     *            copied.
     */
    public FontAlignment(final FontAlignment object) {
        identifier = object.identifier;
        hints = object.hints;
        zones = new ArrayList<GlyphAlignment>(object.zones);
    }

    /**
     * Get the unique identifier of the font definition that the alignment
     * information is for.
     *
     * @return the unique identifier of the font.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the font that this alignment information is for.
     *
     * @param uid
     *            the unique identifier of the DefineFont that contains the
     *            glyphs for the font. Must be in the range 1..65535.
     */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }


    public StrokeWidth getStrokeWidth() {
        StrokeWidth stroke;
        switch (hints) {
        case 0x40:
            stroke = StrokeWidth.MEDIUM;
            break;
        case 0x80:
            stroke = StrokeWidth.THICK;
            break;
        default:
            stroke = StrokeWidth.THIN;
            break;
        }
        return stroke;
    }


    public void setStrokeWidth(final StrokeWidth stroke) {
        switch (stroke) {
        case MEDIUM:
            hints = 0x80;
            break;
        case THICK:
            hints = 0x40;
            break;
        default:
            hints = 0x00;
            break;
        }
    }


    public List<GlyphAlignment> getZones() {
        return zones;
    }


    public void setZones(final List<GlyphAlignment> array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        zones = array;
    }


    public FontAlignment addZone(final GlyphAlignment zone) {
        if (zone == null) {
            throw new IllegalArgumentException();
        }
        zones.add(zone);
        return this;
    }

    /** {@inheritDoc} */
    public FontAlignment copy() {
        return new FontAlignment(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, getStrokeWidth(), zones);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 3;

        for (final GlyphAlignment zone : zones) {
            length += zone.prepareToEncode(context);
        }

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.FONT_ALIGNMENT, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        coder.writeByte(hints);

        for (final GlyphAlignment zone : zones) {
            zone.encode(coder, context);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
