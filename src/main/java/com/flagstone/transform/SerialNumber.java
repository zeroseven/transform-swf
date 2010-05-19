/*
 * SerialNumber.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * SerialNumber is used to add a user-defined serial number into a Flash file.
 */
public final class SerialNumber implements MovieTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "SerialNumber: { number=%s }";
    /** The serial number or identifying string. */
    private String number;

    /** The length of the object, minus the header, when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a SerialNumber object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public SerialNumber(final SWFDecoder coder) throws CoderException {
        length = coder.readHeader();
        number = coder.readString(length - 1, coder.getEncoding());
        coder.readByte();
    }

    /**
     * Creates a SerialNumber action with the specified string.
     *
     * @param aString
     *            an arbitrary string containing the serial number. Must not be
     *            null.
     */
    public SerialNumber(final String aString) {
        setNumber(aString);
    }

    /**
     * Creates and initialises a SerialNumber object using the number copied
     * from another SerialNumber object.
     *
     * @param object
     *            a SerialNumber object from which the serial number will be
     *            copied.
     */
    public SerialNumber(final SerialNumber object) {
        number = object.number;
    }

    /**
     * Get the serial number.
     *
     * @return the string used to identify the movie.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the serial number.
     *
     * @param aString
     *            an arbitrary string containing the serial number. Must not be
     *            null.
     */
    public void setNumber(final String aString) {
        if (aString == null) {
            throw new IllegalArgumentException();
        }
        number = aString;
    }

    /** {@inheritDoc} */
    public SerialNumber copy() {
        return new SerialNumber(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, number);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = context.strlen(number);
        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeHeader(MovieTypes.SERIAL_NUMBER, length);
        coder.writeString(number);
    }
}
