/*
 * FileAttributes.java
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

import java.util.EnumSet;
import java.util.Set;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/** TODO(class). */
public final class MovieAttributes implements MovieTag {

    private static final String FORMAT = "MovieAttributes: { attributes=%s }";

    private int attributes;

    /**
     * Creates and initialises a FileAttributes object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public MovieAttributes(final SWFDecoder coder) throws CoderException {
        coder.readHeader();
        attributes = coder.readByte();
        coder.adjustPointer(24);
    }


    public MovieAttributes(final Set<MovieAttribute>set) {
        setAttributes(set);
    }

    /**
     * Creates and initialises a FileAttributes object using the values copied
     * from another FileAttributes object.
     *
     * @param object
     *            a FileAttributes object from which the values will be
     *            copied.
     */
    public MovieAttributes(final MovieAttributes object) {
        attributes = object.attributes;
    }


    public Set<MovieAttribute> getAttributes() {
        final Set<MovieAttribute>set = EnumSet.noneOf(MovieAttribute.class);

        if ((attributes & 1) != 0) {
            set.add(MovieAttribute.NETWORK_ACCESS);
        }
        if ((attributes & 8) != 0) {
            set.add(MovieAttribute.ACTIONSCRIPT_3);
        }
        if ((attributes & 16) != 0) {
            set.add(MovieAttribute.METADATA);
        }
        return set;
    }


    public void setAttributes(final Set<MovieAttribute>set) {
        attributes = 0;

        if (set.contains(MovieAttribute.NETWORK_ACCESS)) {
            attributes |= 1;
        }
        if (set.contains(MovieAttribute.ACTIONSCRIPT_3)) {
            attributes |= 8;
        }
        if (set.contains(MovieAttribute.METADATA)) {
            attributes |= 16;
        }
    }

    /** {@inheritDoc} */
    public MovieAttributes copy() {
        return new MovieAttributes(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, attributes);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 6;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeHeader(MovieTypes.FILE_ATTRIBUTES, 4);
        coder.writeByte(attributes);
        coder.writeWord(0, 3);
    }
}
