/*
 * DefineFont4.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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
import java.util.Arrays;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

//TODO(class)
public final class DefineFont4 implements DefineTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineFont4: { identifier=%d; "
    		+ "italic=%d; bold=%d; name=%s; data=[%s] }";

    /** The unique identifier for this object. */
    private int identifier;
    private boolean italic;
    private boolean bold;
    private String name;
    private byte[] data;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a DefineFont4 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public DefineFont4(final SWFDecoder coder)
            throws IOException {
        final int start = coder.getPointer();
        length = coder.readLength();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();

        final int bits = coder.readByte();

        italic = (bits & 0x00000002) == 1;
        bold = (bits & 0x00000002) == 1;
        name = coder.readString();
        data = coder.readBytes(new byte[(end - coder.getPointer()) >>> 3]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    public DefineFont4(final int uid, final String fontName,
            final boolean isItalic, final boolean isBold, final byte[] font) {
        setIdentifier(uid);
        setItalic(isItalic);
        setBold(isBold);
        setName(fontName);
        setData(font);
    }

    /**
     * Creates and initialises a DefineFont4 object using the values copied
     * from another DefineFont3 object.
     *
     * @param object
     *            a DefineFont4 object from which the values will be
     *            copied.
     */
    public DefineFont4(final DefineFont4 object) {
        identifier = object.identifier;
        italic = object.italic;
        bold = object.bold;
        name = object.name;
        data = object.data;
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Is the font italicised.
     *
     * @return a boolean indicating whether the font is rendered in italics.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Is the font bold.
     *
     * @return a boolean indicating whether the font is rendered in a bold face.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Returns the name of the font family.
     *
     * @return the name of the font.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the font is italicised.
     *
     * @param aBool
     *            a boolean flag indicating whether the font will be rendered in
     *            italics
     */
    public void setItalic(final boolean aBool) {
        italic = aBool;
    }

    /**
     * Set the font is bold.
     *
     * @param aBool
     *            a boolean flag indicating whether the font will be rendered in
     *            bold face.
     */
    public void setBold(final boolean aBool) {
        bold = aBool;
    }

    /**
     * Set the name of the font.
     *
     * @param aString
     *            the name assigned to the font, identifying the font family.
     *            Must not be null.
     */
    public void setName(final String aString) {
        if (aString == null) {
            throw new IllegalArgumentException();
        }
        name = aString;
    }


    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    public void setData(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public DefineFont4 copy() {
        return new DefineFont4(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, italic, bold,
                name, data.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 3 + context.strlen(name) + data.length;
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {
        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_FONT_4, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        coder.writeBits(0, 5);
        coder.writeBool(data.length > 0);
        coder.writeBool(italic);
        coder.writeBool(bold);
        coder.writeString(name);
        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
