/*
 * Context.java
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

package com.flagstone.transform.coder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.flagstone.transform.CharacterEncoding;

/**
 * Contexts are used to pass information between objects when they are being
 * encoded or decoded.
 */
public class Context {
    /** Flash Version. */
    public static final int VERSION = 1;
    /** Type identifying the current MovieTag being decoded. */
    public static final int TYPE = 2;
    /** Whether the alpha channel should be encoded / decoded. */
    public static final int TRANSPARENT = 3;
    /** Character codes are 16-bits. */
    public static final int WIDE_CODES = 4;
    /** Arrays of fill or line styles can contain more than 255 entries. */
    public static final int ARRAY_EXTENDED = 8;
    /** The glyphs were derived from a Postscript font. */
    public static final int POSTSCRIPT = 9;
    /** The line styles define a scaling stroke. */
    public static final int SCALING_STROKE = 10;
    /** The number of bit used to encode a fill style selection. */
    public static final int FILL_SIZE = 11;
    /** The number of bit used to encode a line style selection. */
    public static final int LINE_SIZE = 12;
    /** The number of bit used to encode a glyph advance. */
    public static final int ADVANCE_SIZE = 13;
    /** The number of bit used to encode a glyph index. */
    public static final int GLYPH_SIZE = 14;
    /** The number of bits used to encode a given shape. */
    public static final int SHAPE_SIZE = 15;
    /** Indicates that this is the last EventHandler to be encoded/decoded. */
    public static final int LAST = 16;
    /** Indicates the flash file is compressed. */
    public static final int COMPRESSED = 17;
    /** Indicates a definition is for menu button. */
    public static final int MENU_BUTTON = 18;

    /** The character encoding used for strings. */
    private String encoding;
    /** The registry containing the objects that perform the decoding. */
    private DecoderRegistry registry;
    /** A table of variables used to pass information between objects. */
    private final transient Map<Integer, Integer> variables;

    /**
     * Create a Context object.
     */
    public Context() {
        encoding = CharacterEncoding.UTF8.toString();
        variables = new LinkedHashMap<Integer, Integer>();
    }

    /**
     * Get character encoding scheme used when encoding or decoding strings.
     *
     * @return the character encoding used for strings.
     */
    public final String getEncoding() {
        return encoding;
    }

    /**
     * Sets the character encoding scheme used when encoding or decoding
     * strings.
     *
     * If the character set encoding is not supported by the Java environment
     * then an UnsupportedCharsetException will be thrown. If the character set
     * cannot be identified then an IllegalCharsetNameException will be thrown.
     *
     * @param charSet
     *            the name of the character set used to encode strings.
     */
    public final void setEncoding(final String charSet) {
        if (!Charset.isSupported(charSet)) {
            throw new UnsupportedCharsetException(charSet);
        }
        encoding = charSet;
    }

    /**
     * Calculates the length of a string when encoded using the specified
     * character set.
     *
     * @param string
     *            the string to be encoded.
     *
     * @return the number of bytes required to encode the string plus 1 for a
     *         terminating null character.
     */

    public final int strlen(final String string) {
        try {
            return string.getBytes(encoding).length + 1;
        } catch (final UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Get the registry containing the decoders for different types of objects.
     * @return the decoder registry.
     */
    public final DecoderRegistry getRegistry() {
        return registry;
    }

    /**
     * Set the registry containing the decoders for different types of objects.
     * @param decoderRegistry the registry containing decoder objects.
     */
    public final void setRegistry(final DecoderRegistry decoderRegistry) {
        registry = decoderRegistry;
    }

    /**
     * Is a variable set.
     * @param key the name of the variable.
     * @return true if the variable is set, false if not.
     */
    public final boolean contains(final Integer key) {
        return variables.containsKey(key);
    }

    /**
     * Delete the context variable.
     *
     * @param key the identifier for the variable.
     */
    public final void remove(final Integer key) {
        variables.remove(key);
    }

    /**
     * Get the value of a variable.
     * @param key the name of the variable.
     * @return the variable value.
     */
    public final Integer get(final Integer key) {
        return variables.get(key);
    }

    /**
     * Set a variable.
     * @param key the name of the variable.
     * @param value the variable value.
     * @return this object.
     */
    public final Context put(final Integer key, final Integer value) {
        variables.put(key, value);
        return this;
    }
}
