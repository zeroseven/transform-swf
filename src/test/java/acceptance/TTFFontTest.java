/*
 * TTFFontTest.java
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

package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineTextField;
import com.flagstone.transform.util.font.Font;
import com.flagstone.transform.util.font.FontFactory;

@RunWith(Parameterized.class)
public final class TTFFontTest {
    
    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir = new File("test/data/ttf/reference");
        final File destDir = new File("test/results/acceptance/TTFFontTest");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".ttf");
            }
        };
        
        String[] files = srcDir.list(filter);
        Object[][] collection = new Object[files.length][2];

        for (int i=0; i<files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
            collection[i][1] = new File(destDir, 
                    files[i].substring(0, files[i].lastIndexOf('.')) + ".swf");
        }
        return Arrays.asList(collection);
    }

    private File sourceFile;
    private File destFile;

    public TTFFontTest(File src, File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void playSound() throws IOException, DataFormatException {

        final int width = 8000;
        final int height = 4000;
        final int margin = 400;
        final int fontSize = 280;

        final int screenWidth = width + margin;
        final int screenHeight = height + margin;
        
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Set<Character> set = new LinkedHashSet<Character>();
        
        for (int i = 0; i < alphabet.length(); i++) {
            set.add(alphabet.charAt(i));
        }

        try {
            final Movie movie = new Movie();
            final FontFactory factory = new FontFactory();
            factory.read(sourceFile);
            List<Font> fonts = factory.getFonts();
            DefineFont2 font = fonts.get(0).defineFont(movie.identifier(), set);

            final DefineTextField text = new DefineTextField(movie.identifier());
            text.setBounds(new Bounds(0, 0, width, height));
            text.setVariableName("var");
            text.setInitialText(alphabet);
            text.setUseFontGlyphs(true);
            text.setFontIdentifier(font.getIdentifier());
            text.setFontHeight(fontSize);
            text.setColor(WebPalette.BLACK.color());

            movie.setFrameSize(new Bounds(0, 0, screenWidth, screenHeight));
            movie.setFrameRate(1.0f);
            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(font);
            movie.add(text);
            movie.add(Place2.show(text, 1, margin, margin));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
