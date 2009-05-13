/*
 * EnableDebugger.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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
 * Enables a movie to be debugged when played using the Flash authoring tool,
 * allowing the variables defined in the arrays of actions specified in object
 * to be inspected. Note that the Flash Player does not support debugging.
 *
 * <p>
 * In order to use the debugger a password must be supplied. When encrypted
 * using the MD5 algorithm it must match the value stored in the password
 * attribute.
 * </p>
 */
public final class EnableDebugger implements MovieTag {
    
    private static final String FORMAT = "EnableDebugger: { password=%s }";

    private String password;

    private transient int length;

    /**
     * Creates and initialises an EnableDebugger object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public EnableDebugger(final SWFDecoder coder) throws CoderException {
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }

        coder.readWord(2, false);
        password = coder.readString();
    }

    /**
     * Creates a EnableDebugger2 object with an MD5 encrypted password.
     *
     * @param password
     *            the string defining the password. Must not be an empty string
     *            or null.
     */
    public EnableDebugger(final String password) {
        setPassword(password);
    }

    /**
     * Creates and initialises an EnableDebugger object using the password
     * from another EnableDebugger object.
     *
     * @param object
     *            a EnableDebugger object from which the password will be
     *            copied.
     */
    public EnableDebugger(final EnableDebugger object) {
        password = object.password;
    }

    /**
     * Returns the MD5 encrypted password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the MD5 encrypted password.
     *
     * @param aString
     *            the string defining the password. Must not be an empty string
     *            or null.
     */
    public void setPassword(final String aString) {
        if (aString == null) {
            throw new IllegalArgumentException(Strings.STRING_IS_NULL);
        }
        if (aString.length() == 0) {
            throw new IllegalArgumentException(Strings.STRING_IS_EMPTY);
        }
        password = aString;
    }

    /** {@inheritDoc} */
    public EnableDebugger copy() {
        return new EnableDebugger(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, password);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2 + coder.strlen(password);

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        if (length > 62) {
            coder.writeWord((MovieTypes.ENABLE_DEBUGGER << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.ENABLE_DEBUGGER << 6) | length, 2);
        }

        coder.writeWord(0, 2);
        coder.writeString(password);
    }
}
