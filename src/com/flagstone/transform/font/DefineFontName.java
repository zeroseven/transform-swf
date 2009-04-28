/*
 * DefineFont2.java
 * Transform
 * 
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.font;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

//TODO(doc)
public final class DefineFontName implements DefineTag {
    private static final String FORMAT = "DefineFontName: { identifier=%d; name=%s; copyright=%s }";

    private int identifier;
    private String name;
    private String copyright;

    private transient int length;

    public DefineFontName(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, false);
        name = coder.readString();
        copyright = coder.readString();

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    public DefineFontName(final int uid, final String name,
            final String copyright) {
        setIdentifier(uid);
        setName(name);
        setCopyright(copyright);
    }

    public DefineFontName(final DefineFontName object) {
        identifier = object.identifier;
        name = object.name;
        copyright = object.copyright;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Returns the name of the font family.
     * 
     * @return the name of the font.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the font.
     * 
     * @param aString
     *            the name assigned to the font, identifying the font family.
     *            Must not be null.
     */
    public void setName(final String aString) {
        if (aString == null) {
            throw new IllegalArgumentException(Strings.STRING_IS_NULL);
        }
        name = aString;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(final String aString) {
        if (aString == null) {
            throw new IllegalArgumentException(Strings.STRING_IS_NULL);
        }
        copyright = aString;
    }

    public DefineFontName copy() {
        return new DefineFontName(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, name, copyright);
    }

    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2 + coder.strlen(name) + coder.strlen(copyright);
        return (length > 62 ? 6 : 2) + length;
    }

    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length > 62) {
            coder.writeWord((MovieTypes.DEFINE_FONT_NAME << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_FONT_NAME << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeString(name);
        coder.writeString(copyright);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
