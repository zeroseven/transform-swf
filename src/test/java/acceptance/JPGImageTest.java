/*
 * JPGIMageTest.java
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
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.util.image.ImageFactory;

@RunWith(Parameterized.class)
public final class JPGImageTest {

    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir = new File("test/data/jpg/reference");
        final File destDir = new File("test/results/acceptance/JPGImageTest");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".jpg");
            }
        };

        String[] files = srcDir.list(filter);
        Object[][] collection = new Object[files.length][2];

        for (int i = 0; i < files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
            collection[i][1] = new File(destDir,
                    files[i].substring(0, files[i].lastIndexOf('.')) + ".swf");
        }
        return Arrays.asList(collection);
    }

    private final File sourceFile;
    private final File destFile;

    public JPGImageTest(final File src, final File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void showImage() {

        try {
            final Movie movie = new Movie();
            final ImageFactory factory = new ImageFactory();
            factory.read(sourceFile);
            final int imageId = movie.nextId();
            final ImageTag image = factory.defineImage(imageId);

            final int xOrigin = image.getWidth() / 2;
            final int yOrigin = image.getHeight() / 2;

            final DefineShape3 shape = factory.defineEnclosingShape(movie
                    .nextId(), imageId, -xOrigin, -yOrigin, null);

            movie.setFrameRate(1.0f);
            movie.setFrameSize(shape.getBounds());
            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(image);
            movie.add(shape);
            movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);

        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
