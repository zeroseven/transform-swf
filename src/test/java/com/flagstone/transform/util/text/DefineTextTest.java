/*
 * DefineTextTest.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.util.text;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.util.font.Font;

public final class DefineTextTest {
    private static File destDir;
    private static Font font;

    @BeforeClass
    public static void initialize() {
        destDir = new File("target/integration-results/Text");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        font = new Font();
        // TODO(code) font.decode(new java.awt.Font("Arial",
        //     java.awt.Font.PLAIN, 12));
    }

    @Test @Ignore
    public void defineText() throws IOException, DataFormatException {
        final File destFile = new File(destDir, "defineText.swf");

        final String alphabet =
            "The quick brown, fox jumped over the lazy dog.";
        final int fontSize = 280;

        final CharacterSet set = new CharacterSet();
        set.add(alphabet);

        final int margin = fontSize;
        int layer = 1;
        final int x = margin;
        final int y = margin;

        final Movie movie = new Movie();
        int uid = 1;

        final int fontId = uid++;
        final DefineFont2 definition = font.defineFont(fontId,
                set.getCharacters());

        MovieHeader header = new MovieHeader();
        header.setFrameRate(1.0f);

        movie.add(header);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(definition);

        final DefineText2 text = null; // TODO(code)
        // TextTable.defineText(movie.newIdentifier(),
        // alphabet, definition, fontSize,
        // ColorTable.black());

        final int textWidth = text.getBounds().getWidth();
        final int textHeight = text.getBounds().getHeight();

        movie.add(text);
        movie.add(Place2.show(text.getIdentifier(), layer++, x, y));
        movie.add(ShowFrame.getInstance());

        header.setFrameSize(new Bounds(0, 0, textWidth + 2 * margin, textHeight
                + 2 * margin));
        movie.encodeToFile(destFile);
    }

    @Test @Ignore
    public void bounds() throws IOException, DataFormatException {
        final File destFile = new File(destDir, "bounds.swf");

        final String alphabet = "abcdefghijklmnopqrstuvwxyz";

        final CharacterSet set = new CharacterSet();
        set.add(alphabet);

        final int fontSize = 280;
        final int lineSpacing = fontSize;
        final int margin = fontSize;
        final int charsPerLine = 32;
        int layer = 1;

        final Movie movie = new Movie();
        int uid = 1;
        int maxWidth = 0;
        int x = margin;
        int y = margin;

        final int fontId = uid++;
        final DefineFont2 definition = font.defineFont(fontId,
                set.getCharacters());

        MovieHeader header = new MovieHeader();
        header.setFrameSize(new Bounds(0, 0, 0, 0));
        header.setFrameRate(1.0f);

        movie.add(header);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(definition);

        for (int i = 0; i < alphabet.length(); i++) {
            final DefineText2 text = null; // TODO(code)
            // TextFactory.defineText(movie.newIdentifier(),
            // alphabet.substring(i,i+1),
            // definition, fontSize,
            // ColorTable.black());

            final int textWidth = text.getBounds().getWidth();
            final int textHeight = text.getBounds().getHeight();
            final int advance = 0; // TODO(code)
            // TextFactory.boundsForText(alphabet.substring(i,i+1),
            // definition, fontSize).getWidth() + 40;

            movie.add(text);
            movie.add(Place2.show(text.getIdentifier(), layer++, x, y));

            if (i % charsPerLine == charsPerLine - 1) {
                maxWidth = x + advance + margin > maxWidth ? x + advance
                        + margin : maxWidth;

                x = margin;
                y += lineSpacing;
            } else {
                x += advance;
            }
        }
        header.setFrameSize(new Bounds(0, 0, maxWidth, y + margin));

        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(destFile);
    }
}
