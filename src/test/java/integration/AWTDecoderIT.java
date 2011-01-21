/*
 * AWTDecoderIT.java
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

package integration;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.HorizontalAlign;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineTextField;
import com.flagstone.transform.util.font.AWTDecoder;
import com.flagstone.transform.util.font.Font;
import com.flagstone.transform.util.text.CharacterSet;
import com.flagstone.transform.util.text.TextTable;

@RunWith(Parameterized.class)
public final class AWTDecoderIT {

    private static final int FONT_POINT_SIZE = 12;

    private static final int FONT_TWIP_SIZE = 720;
    private static final int TEXT_PADDING = 100;
    private static final String ALPHABET = "abcXYZɑ4ßº€éêöã";

    @Parameters
    public static Collection<Object[]> files() {

        final File destDir = new File(
                "target/integration-results/AWTDecoderIT");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final java.awt.Font[] fonts = new java.awt.Font[] {
                new java.awt.Font("Arial",
                        java.awt.Font.PLAIN, FONT_POINT_SIZE),
                new java.awt.Font("Arial",
                        java.awt.Font.BOLD, FONT_POINT_SIZE),
                new java.awt.Font("Arial",
                        java.awt.Font.ITALIC, FONT_POINT_SIZE),
                new java.awt.Font("Courier New",
                        java.awt.Font.PLAIN, FONT_POINT_SIZE),
                new java.awt.Font("Courier New",
                        java.awt.Font.BOLD, FONT_POINT_SIZE),
                new java.awt.Font("Courier New",
                        java.awt.Font.ITALIC, FONT_POINT_SIZE),
                new java.awt.Font("Times New Roman",
                        java.awt.Font.PLAIN, FONT_POINT_SIZE),
                new java.awt.Font("Times New Roman",
                        java.awt.Font.BOLD, FONT_POINT_SIZE),
                new java.awt.Font("Times New Roman",
                        java.awt.Font.ITALIC, FONT_POINT_SIZE)
        };

        Object[][] collection = new Object[fonts.length][2];

        for (int i = 0; i < fonts.length; i++) {
            collection[i][0] = fonts[i];
            collection[i][1] = new File(destDir,
                    fonts[i].getFontName() + ".swf");
        }

        return Arrays.asList(collection);

    }

    private final transient java.awt.Font sourceFont;
    private final transient File destFile;

    public AWTDecoderIT(final java.awt.Font src, final File dst) {
        sourceFont = src;
        destFile = dst;
    }

    @Test
    public void showFont() {
        try {
            final AWTDecoder fontDecoder = new AWTDecoder();
            fontDecoder.read(sourceFont);
            final Font font = fontDecoder.getFonts().get(0);

            final CharacterSet set = new CharacterSet();
            set.add(ALPHABET);

            final Movie movie = new Movie();
            int uid = 1;

            final DefineFont2 nativeFont = new DefineFont2(uid++,
                    sourceFont.getFontName());
            nativeFont.setBold(sourceFont.isBold());
            nativeFont.setItalic(sourceFont.isItalic());

            final DefineFont2 embeddedFont = font.defineFont(uid++,
                    set.getCharacters());
            // Two fonts cannot have the same name.
            embeddedFont.setName("embedded");

            final TextTable textTable = new TextTable(embeddedFont,
            		FONT_TWIP_SIZE);
            final Bounds bounds = Bounds.pad(textTable.boundsForText(ALPHABET),
            		TEXT_PADDING);

            // Create a text field that uses an embedded font.
            final DefineTextField nativeField = new DefineTextField(uid++);
            nativeField.setBounds(bounds);
            nativeField.setAlignment(HorizontalAlign.LEFT);
            nativeField.setFontIdentifier(nativeFont.getIdentifier());
            nativeField.setFontHeight(FONT_TWIP_SIZE);
            nativeField.setEmbedded(false);
            nativeField.setMultiline(true);
            nativeField.setWordWrapped(true);
            nativeField.setInitialText(ALPHABET);

            // Create a text field that uses an embedded font.
            final DefineTextField embeddedField = new DefineTextField(uid++);
            embeddedField.setBounds(bounds);
            embeddedField.setAlignment(HorizontalAlign.LEFT);
            embeddedField.setFontIdentifier(embeddedFont.getIdentifier());
            embeddedField.setFontHeight(FONT_TWIP_SIZE);
            embeddedField.setEmbedded(true);
            embeddedField.setMultiline(true);
            embeddedField.setWordWrapped(true);
            embeddedField.setInitialText(ALPHABET);

            final MovieHeader attrs = new MovieHeader();
            attrs.setFrameSize(Bounds.pad(new Bounds(0, 0, bounds.getWidth(),
                    bounds.getHeight() * 2), 0,
                    TEXT_PADDING * 2, TEXT_PADDING * 6, 0));
            attrs.setFrameRate(1.0f);

            movie.add(attrs);
            movie.add(new Background(WebPalette.WHITE.color()));
            movie.add(nativeFont);
            movie.add(nativeField);
            movie.add(Place2.show(nativeField.getIdentifier(), 1, 0,
                    bounds.getHeight()));
            movie.add(embeddedFont);
            movie.add(embeddedField);
            movie.add(Place2.show(embeddedField.getIdentifier(), 2, 0,
                    bounds.getHeight() * 2 + TEXT_PADDING));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);

        } catch (Throwable t) {
            if (System.getProperty("test.trace") != null) {
                t.printStackTrace(); //NOPMD
            }
            fail(sourceFont.getName());
        }
    }
}
