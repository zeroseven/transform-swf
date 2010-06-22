/*
 * BufferedImageEncoderIT.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.util.image.BufferedImageDecoder;
import com.flagstone.transform.util.image.BufferedImageEncoder;
import com.flagstone.transform.util.image.ImageEncoding;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageRegistry;

@RunWith(Parameterized.class)
public final class BufferedImageEncoderIT {

    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir =
            new File("src/test/resources/bmp-reference");
        final File destDir =
            new File("target/integration-results/BufferedImageEncoder");

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
            collection[i][1] = new File(destDir, files[i]);
        }
        return Arrays.asList(collection);
    }

    private final File sourceFile;
    private final File destFile;

    public BufferedImageEncoderIT(final File src, final File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void showImage() {

        try {
            final BufferedImageDecoder decoder = new BufferedImageDecoder();
            final BufferedImageEncoder encoder = new BufferedImageEncoder();

            final String mimeType = ImageEncoding.PNG.getMimeType();
            ImageRegistry.registerProvider(mimeType, decoder);

            final ImageFactory factory = new ImageFactory();
            factory.read(sourceFile);
            final ImageTag imgIn = factory.defineImage(1);

            encoder.setImage(imgIn);
            BufferedImage imgOut = encoder.getBufferedImage();

            ImageIO.write(imgOut, "bmp", destFile);

        } catch (DataFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
