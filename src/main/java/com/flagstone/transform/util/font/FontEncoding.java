/*
 * FontEncoding.java
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

package com.flagstone.transform.util.font;

/**
 * FontEncoding describes the different fonts that can be decoded from a file.
 *
 * Note: AWT fonts are not included as they are decoded directly from the AWT
 * Font object and not from a file.
 */
enum FontEncoding {
    /** Font definitions stored in a Flash (.swf) file. */
    SWF("swf", new SWFFontDecoder()),
    /** Font definitions stored in a TrueType (.ttf) or OpenType (.otf) file. */
    TTF("ttf", new TTFDecoder());

    /** The identifier for the font format. */
    private final String key;
    /** The FontProvider that can be used to decode the font format. */
    private final FontProvider provider;

    /**
     * Private constructor for the enum.
     *
     * @param type the string representing the font format.
     * @param fontProvider the FontProvider that can be used to decode the
     * font format.
     */
    private FontEncoding(final String type, final FontProvider fontProvider) {
        key = type;
        provider = fontProvider;
    }

    /**
     * Get the identifier for the font format.
     *
     * @return the string identify the font format.
     */
    public String getType() {
        return key;
    }

    /**
     * Get the FontProvider that can be registered in the FontRegistry to
     * decode the font.
     *
     * @return the FontProvider that can be used to decode font of the given
     * format.
     */
    public FontProvider getProvider() {
        return provider;
    }
}
