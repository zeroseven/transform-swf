/*
 * Blend.java
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

package com.flagstone.transform.text;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO(doc)
public enum Language {

    /** The spoken language for traditional Chinese fonts. */
    TRADITIONAL_CHINESE(5),
    /** The spoken language for simplified Chinese fonts. */
    SIMPLIFIED_CHINESE(4),
    /** The spoken language for Japanese fonts. */
    KOREAN(3),
    /** The spoken language for Korean fonts. */
    JAPANESE(2),
    /** The spoken language for Latin fonts. */
    LATIN(1);

    private static final Map<Integer, Language> TABLE = new LinkedHashMap<Integer, Language>();

    static {
        for (final Language language : values()) {
            TABLE.put(language.value, language);
        }
    }

    public static Language fromInt(final int type) {
        return TABLE.get(type);
    }

    private int value;

    private Language(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
