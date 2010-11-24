/*
 * CharacterEncoding.java
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

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CharacterEncoding is used to identify the encoding used for characters in
 * strings stored in the movie.
 */
public enum CharacterEncoding {
    /**
     * Defines that the characters in a font or string are encoded using SJIS
     * standard for representing Kanji characters.
     */
    SJIS("Shift-JIS"),
    /**
     * Defines that the characters in a font or string are encoded using the
     * ANSI (ASCII) standard.
     */
    ANSI("ASCII"),
    /**
     * Defines that the characters in a font or string are encoded using
     * Unicode (UTF-8).
     */
    UTF8("UTF-8"),
    /**
     * Microsoft's extension to Shift-JIS.
     * MS932, windows-932, and csWindows31J are all known aliases of this
     * character set.
     */
    WINDOWS31J("windows-31j"),
    /**
     * MS932 is an alias for Microsoft's extension to Shift-JIS. It is not
     * clear whether the same set of extensions is used in CP932.
     */
    MS932("MS932"),
    /**
     * Defines that the characters in a font or string are encoded using
     * Microsoft's extensions (Code Page 932) to the SJIS standard for
     * representing Kanji characters.
     */
    CP932("CP932");

    /** Table used to map CharSet canonical names to a CharacterEncoding. */
    private static final Map<String, CharacterEncoding> TABLE =
        new LinkedHashMap <String, CharacterEncoding>();

    static {
        for (final CharacterEncoding set : values()) {
            TABLE.put(set.encoding, set);
        }
    }

    /**
     * Get the ChanrcterEncoding that is identified by the canonical name of
     * a CharSet object.
     *
     * @param set
     *            the CharSet.
     *
     * @return the CharacterEncoding that matches the canonical name of the
     * CharSet.
     */
    public static CharacterEncoding fromCharSet(final Charset set) {
    	return TABLE.get(set.name());
    }

    /** Holds character set encoding name used in Java. */
    private String encoding;

    /**
     * Private constructor used for enum values.
     * @param enc the name representing the character encoding.
     */
    private CharacterEncoding(final String enc) {
        encoding = enc;
    }

    /**
     * Get the string used by Java to identify the character encoding.
     *
     * @return the name commonly used to represent the character encoding.
     */
    public String getEncoding() {
        return encoding;
    }
}
