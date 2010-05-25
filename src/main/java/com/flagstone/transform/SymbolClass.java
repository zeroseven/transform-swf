/*
 * SymbolClass.java
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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * SymbolClass is used to export one or more shapes and other objects so they
 * can be used in another Flash file.
 *
 * <p>
 * Since the identifier for an object is only unique within a given Flash file,
 * each object exported must be given a name so it can referenced when it is
 * imported.
 * </p>
 */
//TODO(class)
public final class SymbolClass implements MovieTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "SymbolClass: { objects=%s }";
    /** Table mapping unique identifiers to actionscript 3 classes. */
    private Map<Integer, String> objects;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a SymbolClass object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws IOException
     *             if an error occurs while decoding the data.
     */
    public SymbolClass(final SWFDecoder coder) throws IOException {

        final int start = coder.getPointer();
        length = coder.readLength();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        final int count = coder.readUI16();
        objects = new LinkedHashMap<Integer, String>(count);

        for (int i = 0; i < count; i++) {
            objects.put(coder.readUI16(), coder.readString());
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a SymbolClass object with an empty table.
     */
    public SymbolClass() {
        objects = new LinkedHashMap<Integer, String>();
    }

    /**
     * Creates a SymbolClass object with the specified map.
     *
     * @param map
     *            the table containing identifier/class name pairs.
     */
    public SymbolClass(final Map<Integer, String> map) {
        objects = map;
    }

    /**
     * Creates and initialises a SymbolClass object using the table values
     * copied from another SymbolClass object.
     *
     * @param object
     *            a SymbolClass object from which the table will be
     *            copied.
     */
    public SymbolClass(final SymbolClass object) {
        objects = new LinkedHashMap<Integer, String>(object.objects);
    }

    /**
     * Add a mapping for a unique identifier to an actionscript 3 class.
     * @param uid the unique identifier for the object.
     * @param aString the name of the actionscript 3 class that displays the
     * object.
     * @return this object.
     */
    public SymbolClass add(final int uid, final String aString) {
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
     * Get the table that maps identifiers to actionscript 3 classes.
     * @return the table of identifiers and class names.
     */
    public Map<Integer, String> getObjects() {
        return objects;
    }

    /**
     * Set the table that maps identifiers to actionscript 3 classes.
     * @param aTable the table of identifiers and class names.
     */
    public void setObjects(final Map<Integer, String> aTable) {
        if (aTable == null) {
            throw new IllegalArgumentException();
        }
        objects = aTable;
    }

    /** {@inheritDoc} */
    public SymbolClass copy() {
        return new SymbolClass(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, objects);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {

        length = 2;

        for (final String name : objects.values()) {
            length += 2 + context.strlen(name);
        }

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws IOException {

        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.SYMBOL, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(objects.size());

        for (final Integer identifier : objects.keySet()) {
            coder.writeI16(identifier.intValue());
            coder.writeString(objects.get(identifier));
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
