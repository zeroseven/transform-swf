/*
 * TTFDecoderIT.java
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
import java.io.FilenameFilter;
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
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.util.font.Font;
import com.flagstone.transform.util.font.TTFDecoder;
import com.flagstone.transform.util.text.CharacterSet;
import com.flagstone.transform.util.text.TextTable;

@RunWith(Parameterized.class)
public final class TTFDecoderIT {

    private static final int FONT_TWIP_SIZE = 720;
    private static final int TEXT_PADDING = 100;
    private static final String ALPHABET = "abcXYZɑ4ßº€éêöã";

    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir = new File("src/test/resources/ttf-reference");
        final File destDir = new File(
                "target/integration-results/TTFDecoderIT");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final FilenameFilter filter = new FilenameFilter() {
            @Override
			public boolean accept(final File directory, final String name) {
                return name.endsWith(".ttf");
            }
        };

        final String[] files = srcDir.list(filter);
        final Object[][] collection = new Object[files.length][2];

        for (int i = 0; i < files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
            collection[i][1] = new File(destDir,
                    files[i].substring(0, files[i].lastIndexOf('.')) + ".swf");
        }
        return Arrays.asList(collection);
    }

    private final transient File sourceFile;
    private final transient File destFile;

    public TTFDecoderIT(final File src, final File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void showFont() {
        try {
            final TTFDecoder fontDecoder = new TTFDecoder();
            fontDecoder.read(sourceFile);
            final Font font = fontDecoder.getFonts().get(0);

            final CharacterSet set = new CharacterSet();
            set.add(ALPHABET);

            final Movie movie = new Movie();
            int uid = 1;

            final DefineFont2 definition = font.defineFont(uid++,
                    set.getCharacters());
            final TextTable textTable = new TextTable(definition,
            		FONT_TWIP_SIZE);
            final Bounds bounds = Bounds.pad(
                    textTable.boundsForText(ALPHABET), TEXT_PADDING);
            final DefineText2 text = textTable.defineText(uid++,
            		ALPHABET, WebPalette.BLACK.color());

            final MovieHeader attrs = new MovieHeader();
            attrs.setFrameSize(bounds);
            attrs.setFrameRate(1.0f);

            movie.add(attrs);
            movie.add(new Background(WebPalette.WHITE.color()));
            movie.add(definition);
            movie.add(text);
            movie.add(Place2.show(text.getIdentifier(), 1, 0, 0));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);

        } catch (Exception e) {
            if (System.getProperty("test.trace") != null) {
                e.printStackTrace(); //NOPMD
            }
            fail(sourceFile.getName());
        }
    }
}
