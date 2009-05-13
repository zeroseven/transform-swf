/*
 * DefineData.java
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
package com.flagstone.transform;

import java.util.Arrays;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * DefineData is used to embed binary data in a Flash file.
 *
 * <p>
 * DefineData can also be used to initialize Actionscript3 classes when they are
 * loaded into the Flash Player. The table in a SymbolClass object maps class
 * names to object definitions in the movie using a unique identifier. If the
 * class is a sub-class of ByteArray then the data from the DefineData object
 * with a matching identifier will be used to initialize the class.
 * </p>
 *
 * @see SymbolClass
 */
public final class DefineData implements DefineTag {

    private static final String FORMAT = "DefineData: {"
            + "identifier=%d; data=byte[%d] {...} }";

    private int identifier;
    private byte[] data;

    private transient int length;

    /**
     * Creates and initialises a DefineData object using values encoded in the
     * Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineData(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();
        length = coder.readWord(2, false) & SWFDecoder.MASK_LENGTH;

        if (length > SWFDecoder.MAX_LENGTH) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        coder.adjustPointer(32);
        data = coder.readBytes(new byte[length - 6]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineData object with the specified data.
     *
     * @param uid
     *            the unique identifier used to reference this object.
     * @param data
     *            the data to initialize the object.
     */
    public DefineData(final int uid, final byte[] data) {
        setIdentifier(uid);
        setData(data);
    }

    /**
     * Creates a DefineData initialize with a copy of the data from another
     * object.
     *
     * @param object
     *            a DefineData object used to initialize this one.
     */
    public DefineData(final DefineData object) {
        identifier = object.identifier;
        data = Arrays.copyOf(object.data, object.data.length);
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Returns the array of bytes that will be embedded in the Flash file.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the array of bytes that will be embedded in the Flash file.
     *
     * @param bytes
     *            an array of bytes that contain the encoded binary data. Must
     *            not be null.
     */
    public void setData(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException(Strings.DATA_IS_NULL);
        }
        data = bytes;
    }

    /** {@inheritDoc} */
    public DefineData copy() {
        return new DefineData(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, data.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 6 + data.length;
        return (length > SWFEncoder.MAX_LENGTH ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();
        final int type = MovieTypes.DEFINE_BINARY_DATA << SWFEncoder.LENGTH_BITS;

        if (length > SWFEncoder.MAX_LENGTH) {
            coder.writeWord(type | SWFEncoder.EXTENDED, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord(type | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeWord(0, 4);
        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
