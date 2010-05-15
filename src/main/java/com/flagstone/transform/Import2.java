/*
 * Import2.java
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
 * Import2 is used to import shapes and other objects from another Flash file
 * referenced by a URL.
 *
 * <p>
 * To provide a degree of security the Flash Player will only import files that
 * originate from the same domain as the file that it is currently playing. For
 * example if the Flash file being shown was loaded from
 * www.mydomain.com/flash.swf then the file contains the exported objects must
 * reside somewhere at www.mydomain.com. This prevents a malicious Flash file
 * from loading files from an unknown third party.
 * </p>
 *
 * <p>
 * Since the identifier for an object is only unique within a given Flash file,
 * imported objects are referenced by a name assigned when the object is
 * exported.
 * </p>
 *
 * <p>
 * Import2 was added in Flash 8. It currently has the same functionality as
 * Import.
 * </p>
 *
 * @see Export
 * @see Import
 */
public final class Import2 implements MovieTag {

    private static final String FORMAT = "Import2: { url=%s; objects=%s }";

    private String url;
    private Map<Integer, String> objects;

    private transient int length;

    /**
     * Creates and initialises an Import2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public Import2(final SWFDecoder coder) throws CoderException {
        length = coder.readHeader();
        url = coder.readString();
        coder.adjustPointer(Coder.BITS_PER_SHORT);

        final int count = coder.readUI16();
        objects = new LinkedHashMap<Integer, String>();

        for (int i = 0; i < count; i++) {
            objects.put(coder.readUI16(), coder.readString());
        }
    }

    /**
     * Creates a Import2 object with an empty table.
     */
    public Import2() {
        objects = new LinkedHashMap<Integer, String>();
    }

    /**
     * Creates an Import2 object that imports an object from the specified file.
     *
     * @param aUrl
     *            the URL referencing the file to be imported.
     *
     * @param map
     *            the table to add the identifier-name pairs of the objects that
     *            will be imported.
     */
    public Import2(final String aUrl, final Map<Integer, String> map) {
        setUrl(aUrl);
        objects = map;
    }

    /**
     * Creates and initialises an Import2 object using the values copied
     * from another Import2 object.
     *
     * @param object
     *            an Import2 object from which the values will be
     *            copied.
     */
    public Import2(final Import2 object) {
        url = object.url;
        objects = new LinkedHashMap<Integer, String>(object.objects);
    }

    /**
     * Adds the identifier and name to the list of objects to be imported.
     *
     * @param uid
     *            the identifier of the object to be imported. Must be in the
     *            range 1..65535.
     * @param aString
     *            the name of the imported object to allow it to be referenced.
     *            Must not be null or an empty string.
     */
    public Import2 add(final int uid, final String aString) {
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
     * Returns the URL of the file containing the object to be imported. Limited
     * security is provided by requiring that the URL must be in the same domain
     * or sub-domain as the URL of the movie which contains this object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the file containing the imported objects. The URL must be
     * in the same sub-domain and relative to the URL of the file containing the
     * Import object.
     *
     * @param aString
     *            a URL relative to the URL of the file containing the Import
     *            object. Must not be null or an empty string.
     */
    public void setUrl(final String aString) {
        if (aString == null || aString.length() == 0) {
            throw new IllegalArgumentException();
        }
        url = aString;
    }

    /**
     * Returns the table of objects to be imported.
     */
    public Map<Integer, String> getObjects() {
        return objects;
    }

    /**
     * Sets the table of objects to be imported.
     *
     * @param aTable
     *            the table of objects being imported. Must not be null.
     */
    public void setObjects(final Map<Integer, String> aTable) {
        objects = aTable;
    }

    /** {@inheritDoc} */
    public Import2 copy() {
        return new Import2(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, url, objects);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        // CHECKSTYLE:OFF
        length = 4 + context.strlen(url);

        for (final String name : objects.values()) {
            length += 2 + context.strlen(name);
        }

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
        // CHECKSTYLE:ON
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        coder.writeHeader(MovieTypes.IMPORT_2, length);
        coder.writeString(url);
        coder.writeByte(1);
        coder.writeByte(0);
        coder.writeI16(objects.size());

        for (final Integer identifier : objects.keySet()) {
            coder.writeI16(identifier.intValue());
            coder.writeString(objects.get(identifier));
        }
    }
}
