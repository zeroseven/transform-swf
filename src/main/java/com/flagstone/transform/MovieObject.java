/*
 * MovieObject.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * MovieObject is used to represent any object decoded from a Movie that is not
 * directly supported by Transform.
 *
 * <p>
 * This allow a certain amount of forward compatibility where file which use a
 * version of Flash greater than the one supported by Transform can be decoded
 * and encoded.
 * </p>
 */
//TODO(class)
public final class MovieObject implements MovieTag {

    private static final String FORMAT = "MovieObject: { type=%d;"
            + " data=byte[%d] {...} }";

    private final transient int type;
    private final transient byte[] data;

    private transient int length;

    /**
     * Creates and initialises a MovieObject object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public MovieObject(final SWFDecoder coder) throws CoderException {
        type = coder.scanUnsignedShort() >>> 6;
        length = coder.readHeader();
        data = coder.readBytes(new byte[length]);
    }


    public MovieObject(final int aType, final byte[] bytes) {
        type = aType;

        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        data = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Creates and initialises a MovieObject object using the values copied
     * from another MovieObject object.
     *
     * @param object
     *            a MovieObject object from which the values will be
     *            copied.
     */
    public MovieObject(final MovieObject object) {
        type = object.type;
        data = object.data;
    }


    public int getType() {
        return type;
    }

    /**
     * Returns a copy of the encoded data for the movie tag object.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /** {@inheritDoc} */
    public MovieObject copy() {
        return new MovieObject(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, type, data.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = data.length;
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeHeader(type, length);
        coder.writeBytes(data);
    }
}
