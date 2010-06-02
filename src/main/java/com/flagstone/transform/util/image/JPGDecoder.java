/*
 * JPGDecoder.java
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

package com.flagstone.transform.util.image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.BigDecoder;
import com.flagstone.transform.image.DefineJPEGImage2;
import com.flagstone.transform.image.ImageTag;

/**
 * JPGDecoder decodes JPEG images so they can be used in a Flash file.
 */
public final class JPGDecoder implements ImageProvider, ImageDecoder {

    /** Message used to signal that the image cannot be decoded. */
    private static final String BAD_FORMAT = "Unsupported format";

    /** The width of the image in pixels. */
    private transient int width;
    /** The height of the image in pixels. */
    private transient int height;
    /** The image data. */
    private transient byte[] image = new byte[0];

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
        read(new FileInputStream(file));
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();

        if (!connection.getContentType().equals("image/bmp")) {
            throw new DataFormatException(BAD_FORMAT);
        }

        final int length = connection.getContentLength();

        if (length < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        read(url.openStream());
    }

    /** {@inheritDoc} */
    public ImageTag defineImage(final int identifier) {
        return new DefineJPEGImage2(identifier, image);
    }

    /** {@inheritDoc} */
    public ImageDecoder newDecoder() {
        return new JPGDecoder();
    }

    /** {@inheritDoc} */
     public void read(final InputStream stream)
                 throws DataFormatException, IOException {

         BigDecoder coder = new BigDecoder(stream);

         int marker;
         int length;

         do {
             marker = coder.readUI16();
             if (marker == 0xFFD8) {
                 copyTag(marker, 0, coder);
             } else if (marker == 0xFFC0) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFC2) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFC4) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFDB) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFDD) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFDA) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else if (marker == 0xFFD9) {
                 copyTag(marker, 0, coder);
             } else if ((marker & 0xFFE0) == 0xFFE0) {
                 length = coder.readUI16();
                 copyTag(marker, length, coder);
             } else {
                 copyTag(marker, 0, coder);
             }
         } while (marker != 0xFFD9);

         if (!jpegInfo()) {
             throw new DataFormatException(BAD_FORMAT);
         }
    }

     private void copyTag(final int marker, final int length,
             final BigDecoder coder) throws IOException {
         byte[] bytes;
         if (length > 0) {
             bytes = new byte[length+2];
         } else {
             bytes = new byte[2];
         }
         bytes[0] = (byte) (marker >> 8);
         bytes[1] = (byte) marker;

         if (length > 0) {
             bytes[2] = (byte) (length >> 8);
             bytes[3] = (byte) length;
             coder.readBytes(bytes, 4, length-2);
         }
         int imgLength = image.length;
         image = Arrays.copyOf(image, imgLength + bytes.length);
         System.arraycopy(bytes, 0, image, imgLength, bytes.length);
     }

     /** {@inheritDoc} */
    public int getWidth() {
        return width;
    }

    /** {@inheritDoc} */
    public int getHeight() {
        return height;
    }

    /** {@inheritDoc} */
    public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Decode the width and height from a JPEG image.
     * @return true if the image is in JPEG format and the width and height
     * were decoded.
     */
    private boolean jpegInfo() {
        ByteArrayInputStream stream = new ByteArrayInputStream(image);
        final BigDecoder coder = new BigDecoder(stream);
        boolean result;
        try {
            if (coder.readUI16() == 0xffd8) {
                int marker;

                do {
                    marker = coder.readUI16();

                    if ((marker & 0xff00) == 0xff00) {
                        if ((marker >= 0xffc0) && (marker <= 0xffcf)
                                && (marker != 0xffc4) && (marker != 0xffc8)) {
                            coder.adjustPointer(24);
                            height = coder.readUI16();
                            width = coder.readUI16();
                            break;
                        } else {
                            coder.adjustPointer((coder.readUI16() - 2) << 3);
                        }
                    }

                } while ((marker & 0xff00) == 0xff00);

                result = true;
            } else {
                result = false;
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
}
