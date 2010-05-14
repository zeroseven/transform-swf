/*
 * Export.java
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

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * Export is used to export one or more shapes and other objects so they can be
 * used in another Flash file.
 *
 * <p>
 * Since the identifier for an object is only unique within a given Flash file,
 * each object exported is assigned a name so it can referenced when it is
 * imported.
 * </p>
 */
public final class Export implements MovieTag {

    private static final String FORMAT = "Export: { objects=%s }";

    private Map<Integer, String> objects;

    private transient int length;

    /**
     * Creates and initialises an Export object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public Export(final SWFDecoder coder) throws CoderException {
        length = coder.readHeader();
        final int count = coder.readUI16();
        objects = new LinkedHashMap<Integer, String>();

        for (int i = 0; i < count; i++) {
            add(coder.readUI16(), coder.readString());
        }
    }

    /**
     * Creates an Export object with an empty map.
     */
    public Export() {
        objects = new LinkedHashMap<Integer, String>();
    }

    /**
     * Creates an Export object with the specified map.
     *
     * @param map
     *            the table containing identifier/name pairs for the objects
     *            that will be exported from the movie.
     */
    public Export(final Map<Integer, String> map) {
        objects = map;
    }

    /**
     * Creates and initialises an Export object using the values copied
     * from another Export object.
     *
     * @param object
     *            a Export object from which the values will be
     *            copied.
     */
    public Export(final Export object) {
        objects = new LinkedHashMap<Integer, String>(object.objects.size());
        objects.putAll(object.objects);
    }

    /**
     * Adds the identifier and name to the list of objects to be exported.
     *
     * @param uid
     *            the identifier of the object to be exported.
     * @param aString
     *            the name of the exported object to allow it to be referenced.
     *            The name must not be null or an empty string.
     */
    public Export add(final int uid, final String aString) {
        if ((uid < 1) || (uid > SWF.MAX_IDENTIFIER)) {
             throw new IllegalArgumentRangeException(
                     1, SWF.MAX_IDENTIFIER, uid);
        }
        if (aString == null || aString.length() == 0) {
            throw new IllegalArgumentException();
        }

        objects.put(uid, aString);
        return this;
    }

    /**
     * Returns the table of objects to be exported.
     */
    public Map<Integer, String> getObjects() {
        return objects;
    }

    /**
     * Sets the table of objects to be exported.
     *
     * @param aTable
     *            the table of objects being imported. Must not be null.
     */
    public void setObjects(final Map<Integer, String> aTable) {
        if (aTable == null) {
            throw new IllegalArgumentException();
        }
        objects = aTable;
    }

    /** {@inheritDoc} */
    public Export copy() {
        return new Export(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, objects);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2;

        for (final Integer identifier : objects.keySet()) {
            length += 2 + coder.strlen(objects.get(identifier));
        }
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeHeader(MovieTypes.EXPORT, length);
        coder.writeI16(objects.size());

        for (final Integer identifier : objects.keySet()) {
            coder.writeI16(identifier.intValue());
            coder.writeString(objects.get(identifier));
        }
    }
}
