/*
 * Symbol.java
 * Transform
 * 
 * Copyright (c) 2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform;

import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc) Review
/**
 * Symbol is used to SYMBOL one or more shapes and other objects so they can be
 * used in another Flash file.
 * 
 * <p>
 * Since the identifier for an object is only unique within a given Flash file,
 * each object SYMBOLed must be given a name so it can referenced when it is
 * imported.
 * </p>
 */
public final class SymbolClass implements MovieTag {

    private static final String FORMAT = "SymbolClass: { objects=%s }";

    private Map<Integer, String> objects;

    private transient int length;

    public SymbolClass(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        final int count = coder.readWord(2, false);
        objects = new LinkedHashMap<Integer, String>(count);

        for (int i = 0; i < count; i++) {
            objects.put(coder.readWord(2, false), coder.readString());
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates an SYMBOL object with an empty array.
     * 
     * @param map
     *            the table containing identifier/name pairs for the objects
     *            that will be SYMBOLed from the movie.
     */
    public SymbolClass(final Map<Integer, String> map) {
        objects = map;
    }

    /**
     * Creates an SYMBOL object that SYMBOLs the object with the specified
     * identifier. The SYMBOLed object is assigned the specified name to allow
     * it to be referenced in files importing the object.
     * 
     * @param uid
     *            the identifier of the object to be SYMBOLed. Must be in the
     *            range 1..65535.
     * @param aString
     *            the name of the SYMBOLed object to allow it to be referenced.
     *            Must not be an empty string or null.
     */
    public SymbolClass(final int uid, final String aString) {
        objects = new LinkedHashMap<Integer, String>();
        add(uid, aString);
    }

    public SymbolClass(final SymbolClass object) {
        objects = new LinkedHashMap<Integer, String>(object.objects);
    }

    /**
     * Adds the identifier and name to the list of objects to be SYMBOLed.
     * 
     * @param uid
     *            the identifier of the object to be SYMBOLed.
     * @param aString
     *            the name of the SYMBOLed object to allow it to be referenced.
     *            The name must not be null or an empty string.
     */
    public void add(final int uid, final String aString) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        if ((aString == null) || (aString.length() == 0)) {
            throw new IllegalArgumentException(Strings.STRING_NOT_SET);
        }
        objects.put(uid, aString);
    }

    /**
     * Returns the table of objects to be SYMBOLed.
     */
    public Map<Integer, String> getObjects() {
        return objects;
    }

    /**
     * Sets the table of objects to be SYMBOLed.
     * 
     * @param aTable
     *            the table of objects being imported. Must not be null.
     */
    public void setObjects(final Map<Integer, String> aTable) {
        if (aTable == null) {
            throw new IllegalArgumentException(Strings.TABLE_IS_NULL);
        }
        objects = aTable;
    }

    // TODO(doc)
    public SymbolClass copy() {
        return new SymbolClass(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, objects);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        length = 2;

        for (final String name : objects.values()) {
            length += 2 + coder.strlen(name);
        }

        return (length > 62 ? 6 : 2) + length;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();

        if (length > 62) {
            coder.writeWord((MovieTypes.SYMBOL << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.SYMBOL << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(objects.size(), 2);

        for (final Integer identifier : objects.keySet()) {
            coder.writeWord(identifier.intValue(), 2);
            coder.writeString(objects.get(identifier));
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
