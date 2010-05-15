/*
 * DefineData.java
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
package com.flagstone.transform;

import java.util.Arrays;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

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

    /** Format string used in toString() method. */
    private static final String FORMAT = "DefineData: { identifier=%d;"
            + " data=byte[%d] {...} }";

    /** Unique identifier for this definition. */
    private int identifier;
    /** Binary encoded data. */
    private byte[] data;

    /** Length of the encoded object. */
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
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();
        coder.readUI32();
        data = coder.readBytes(new byte[(end - coder.getPointer())
                                        >>> Coder.BITS_TO_BYTES]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >>> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a DefineData object with the specified data.
     *
     * @param uid
     *            the unique identifier used to reference this object.
     * @param bytes
     *            the data to initialize the object.
     */
    public DefineData(final int uid, final byte[] bytes) {
        setIdentifier(uid);
        setData(bytes);
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
     * Returns a copy of the array of bytes that will be embedded in the
     * Flash file.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
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
            throw new IllegalArgumentException();
        }
        data = Arrays.copyOf(bytes, bytes.length);
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
    public int prepareToEncode(final Context context) {
        //CHECKSTYLE:OFF
        length = 6 + data.length;
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
        //CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_BINARY_DATA, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        coder.writeI32(0);
        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
