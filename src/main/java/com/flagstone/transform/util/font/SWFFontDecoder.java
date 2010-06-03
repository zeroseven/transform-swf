/*
 * SWFFontDecoder.java
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.font.DefineFont;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.font.FontInfo;
import com.flagstone.transform.font.FontInfo2;
import com.flagstone.transform.shape.Shape;

/**
 * SWFFontDecoder decodes Flash movie Fonts so they can be re-used in a
 * Flash file.
 *
 * <p>
 * Using an existing Flash font definition is the most interesting. Fonts can
 * initially be created using AWT Font objects or TrueType files and all the
 * visible characters included. If the generated Flash definition is saved to a
 * file it can easily and quickly be loaded. Indeed the overhead of parsing an
 * AWT or TrueType font is significant (sometimes several seconds) so creating
 * libraries of "pre-parsed" flash fonts is the preferred way of use fonts.
 * </p>
 */
public final class SWFFontDecoder implements FontProvider, FontDecoder {

    /** The table of fonts, indexed by unique identifier. */
    private final transient Map<Integer, Font>fonts
                = new LinkedHashMap<Integer, Font>();

    /** {@inheritDoc} */
    public FontDecoder newDecoder() {
        return new SWFFontDecoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        Movie movie = new Movie();
        movie.decodeFromFile(file);
        decode(movie);
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        Movie movie = new Movie();
        movie.decodeFromUrl(url);
        decode(movie);
    }

    /** {@inheritDoc} */
    public List<Font> getFonts() {
        return new ArrayList<Font>(fonts.values());
    }

    /**
     * Decode a font from a stream.
     * @param stream the stream containing the encoded font.
     * @throws IOException if an error occurs while decoding the font data.
     * @throws DataFormatException if the font is not in a supported format.
     */
    private void decode(final Movie movie)
            throws IOException, DataFormatException {

        fonts.clear();

        final SWFFontDecoder decoder = new SWFFontDecoder();

        for (MovieTag obj : movie.getObjects()) {
            if (obj instanceof DefineFont2) {
                decoder.decode((DefineFont2) obj);
            }
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param glyphs
     *            a DefineFont object which contains the definition of the
     *            glyphs.
     */
    public void decode(final DefineFont glyphs) {

        final Font font = new Font();

        font.setAscent(0);
        font.setDescent(0);
        font.setLeading(0);

        final int glyphCount = glyphs.getShapes().size();

        font.setMissingGlyph(0);
        font.setNumberOfGlyphs(glyphCount);
        font.setHighestChar((char) glyphCount);

        if (glyphCount > 0) {

            Shape shape;

            for (int i = 0; i < glyphCount; i++) {
                shape = glyphs.getShapes().get(i);
                font.addGlyph((char) i, new Glyph(shape));
            }
        }

        fonts.put(glyphs.getIdentifier(), font);
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param info
     *            a FontInfo object that contains information on the font name,
     *            weight, style and character codes.
     */
    public void decode(final FontInfo info) {

        final Font font = fonts.get(info.getIdentifier());

        font.setFace(new FontFace(info.getName(),
                info.isBold(), info.isItalic()));

        font.setEncoding(info.getEncoding());

        final int glyphCount = font.getNumberOfGlyphs();
        final int highest = info.getCodes().get(glyphCount);

        font.setHighestChar((char) highest);

        if (glyphCount > 0) {

            Glyph glyph;
            int code;

            for (int i = 0; i < glyphCount; i++) {
                glyph = font.getGlyph(i);
                code = info.getCodes().get(i);

                font.addGlyph((char) code, glyph);
            }
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param info
     *            a FontInfo2 object that contains information on the font name,
     *            weight, style and character codes.
     */
    public void decode(final FontInfo2 info) {

        final Font font = fonts.get(info.getIdentifier());

        font.setFace(new FontFace(info.getName(),
                info.isBold(), info.isItalic()));

        font.setEncoding(info.getEncoding());

        final int glyphCount = font.getNumberOfGlyphs();
        final int highest = info.getCodes().get(glyphCount);

        font.setHighestChar((char) highest);

        if (glyphCount > 0) {

            Glyph glyph;
            int code;

            for (int i = 0; i < glyphCount; i++) {
                glyph = font.getGlyph(i);
                code = info.getCodes().get(i);

                font.addGlyph((char) code, glyph);
            }
        }
    }

    /**
     * Initialise this object with the information from a flash font definition.
     *
     * @param object
     *            a DefineFont2 object that contains information on the font
     *            name, weight, style and character codes as well as the glyph
     *            definitions.
     */
    public void decode(final DefineFont2 object) {

        final Font font = new Font();

        font.setFace(new FontFace(object.getName(),
                object.isBold(), object.isItalic()));

        font.setEncoding(object.getEncoding());
        font.setAscent(object.getAscent());
        font.setDescent(object.getDescent());
        font.setLeading(object.getLeading());

        final int glyphCount = object.getShapes().size();
        final int highest = object.getCodes().get(glyphCount);

        font.setMissingGlyph(0);
        font.setNumberOfGlyphs(glyphCount);
        font.setHighestChar((char) highest);

        if (glyphCount > 0) {

            Shape shape;
            Bounds bounds = null;
            int advance;
            int code;

            for (int i = 0; i < glyphCount; i++) {
                shape = object.getShapes().get(i);

                if (object.getBounds() != null) {
                     bounds = object.getBounds().get(i);
                }
                if (object.getAdvances() == null) {
                    advance = 0;
                } else {
                    advance = object.getAdvances().get(i);
                }
                code = object.getCodes().get(i);

                font.addGlyph((char) code, new Glyph(shape, bounds, advance));
            }
        }

        fonts.put(object.getIdentifier(), font);
    }
}
