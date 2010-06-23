/*
 * BufferedImageBMPDecoderIT.java
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

package integration;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.DataFormatException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.shape.ShapeTag;
import com.flagstone.transform.util.image.BufferedImageDecoder;
import com.flagstone.transform.util.image.ImageEncoding;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageRegistry;
import com.flagstone.transform.util.image.ImageShape;

@RunWith(Parameterized.class)
public final class BufferedImageBMPDecoderIT {

    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir =
            new File("src/test/resources/bmp-reference");
        final File destDir =
            new File("target/integration-results/BufferedImage");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".bmp");
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

    public BufferedImageBMPDecoderIT(final File src, final File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void showImage() {

        try {
            final BufferedImageDecoder decoder = new BufferedImageDecoder();
            final String mimeType = ImageEncoding.PNG.getMimeType();
            ImageRegistry.registerProvider(mimeType, decoder);

            final Movie movie = new Movie();
            int uid = 1;

            final ImageFactory factory = new ImageFactory();
            factory.read(sourceFile);
            final int imageId = uid++;
            final ImageTag image = factory.defineImage(imageId);

            final int xOrigin = image.getWidth() / 2;
            final int yOrigin = image.getHeight() / 2;

            final ShapeTag shape = new ImageShape().defineShape(uid++,
                    image, -xOrigin, -yOrigin, null);

            MovieHeader attrs = new MovieHeader();
            attrs.setFrameRate(1.0f);
            attrs.setFrameSize(shape.getBounds());

            movie.add(attrs);
            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(image);
            movie.add(shape);
            movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);

        } catch (DataFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
