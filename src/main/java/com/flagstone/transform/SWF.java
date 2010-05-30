/*
 * SWF.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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

/**
 * SWF contains definitions of constants used throughout the framework.
 */
public final class SWF {
    /**
     * The prime number used to generate the value in the hashCode() method
     * of immutable objects.
     */
    public static final int PRIME = 31;
    /**
     * The version of Flash supported.
     */
    public static final int VERSION = 10;
    /**
     * The lowest value that can be used as a unique identifier.
     */
    public static final int MIN_IDENTIFIER = 1;
    /**
     * The highest value that can be used as a unique identifier.
     */
    public static final int MAX_IDENTIFIER = 65535;
    /**
     * The highest value that can be used as a layer number on the display list.
     */
    public static final int MAX_LAYER = 65535;
    /**
     * The highest value that can be used as a morphing ratio.
     */
    public static final int MAX_MORPH = 65535;
    /**
     * The highest frame number in a given scene.
     */
    public static final int MAX_FRAME = 65535;
    /**
     * The highest character code that can be defined in fonts.
     */
    public static final int MAX_CHARACTER = 65535;
    /**
     * The maximum width of an image in pixels.
     */
    public static final int MAX_WIDTH = 65535;
    /**
     * The maximum height of an image in pixels.
     */
    public static final int MAX_HEIGHT = 65535;
    /**
     * The minimum value that can be used to specify the distance to the origin
     * of the next glyph.
     */
    public static final int MIN_ADVANCE = -32768;
    /**
     * The maximum value that can be used to specify the distance to the origin
     * of the next glyph.
     */
    public static final int MAX_ADVANCE = 32767;
    /**
     * The minimum value that can be used to specify the height of a glyph
     * above the baseline for a font.
     */
    public static final int MIN_ASCENT = -32768;
    /**
     * The maximum value that can be used to specify the height of a glyph
     * above the baseline for a font.
     */
    public static final int MAX_ASCENT = 32767;
    /**
     * The minimum value that can be used to specify the distance that a glyph
     * drops below the baseline for a font.
     */
    public static final int MIN_DESCENT = -32768;
    /**
     * The maximum value that can be used to specify the distance that a glyph
     * drops below the baseline for a font.
     */
    public static final int MAX_DESCENT = 32767;
    /**
     * The minimum value that can be used to specify the distance between
     * successive lines of text.
     */
    public static final int MIN_LEADING = -32768;
    /**
     * The maximum value that can be used to specify the distance between
     * successive lines of text.
     */
    public static final int MAX_LEADING = 32767;
    /**
     * The minimum value that can be used to adjust the distance between
     * successive glyphs.
     */
    public static final int MIN_KERNING = -32768;
    /**
     * The maximum value that can be used to adjust the distance between
     * successive glyphs.
     */
    public static final int MAX_KERNING = 32767;
    /**
     * The minimum offset that can be used to position text within the bounding
     * box.
     */
    public static final int MIN_OFFSET = -32768;
    /**
     * The maximum offset that can be used to position text within the bounding
     * box.
     */
    public static final int MAX_OFFSET = 32767;
    /**
     * The maximum number of glyphs that can be defined in a font.
     */
    public static final int MAX_GLYPHS = 65536;
    /**
     * The maximum size, in twips, of a font. Equivalent to 20 times the point
     * size.
     */
    public static final int MAX_FONT_SIZE = 65535;
    /**
     * The minimum coordinate in along the x or y axes.
     */
    public static final int MIN_COORD = -65536;
    /**
     * The maximum coordinate in along the x or y axes.
     */
    public static final int MAX_COORD = 65535;
    /**
     * Maximum number of gradient records.
     */
    public static final int MAX_GRADIENTS = 15;

    /** Class contains only constants. */
    private SWF() {
        // This class only contains constants.
    }

}
