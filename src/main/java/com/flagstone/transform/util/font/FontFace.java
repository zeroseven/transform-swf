/*
 * FontFace.java
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

/** TODO(class). */
public final class FontFace {

    private final transient String name;
    private final transient boolean bold;
    private final transient boolean italic;

    /** TODO(method). */
    public FontFace(final String name, final boolean isBold, final boolean isItalic) {
        this.name = name;
        this.bold = isBold;
        this.italic = isItalic;
    }

    /** TODO(method). */
    public FontFace(final String name, final int style) {
        this.name = name;
        this.bold = (style & java.awt.Font.BOLD) != 0;
        this.italic = (style & java.awt.Font.ITALIC) != 0;
    }

    /**
     * Returns the (family) name of the font.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the font weight is bold or false if it is normal.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Returns true if the font style is italic or false if it is normal.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Returns the code for the font style, as defined in the AWT Font class,
     * either Font.PLAIN or a combination of Font.BOLD and Font.ITALIC.
     */
    public int getStyle() {
        int style = java.awt.Font.PLAIN;

        if (bold) {
            style |= java.awt.Font.BOLD;
        } else if (italic) {
            style |= java.awt.Font.ITALIC;
        }

        return style;
    }

    /**
     * Returns name of the font face which contains the name of the font
     * followed by "Bold" for bold fonts and "Italic" for fonts with an italic
     * style.
     */
    @Override
    public String toString() {
        return name + (bold ? " Bold" : "") + (italic ? " Italic" : "");
    }

    @Override
    public boolean equals(final Object object) {
        boolean result;
        FontFace key;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof FontFace) {
            key = (FontFace) object;
            result = name.equals(name) && bold == key.bold
                    && italic == key.italic;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return (name.hashCode() * 31 + Boolean.valueOf(bold).hashCode()) * 31
                + Boolean.valueOf(italic).hashCode();
    }
}
