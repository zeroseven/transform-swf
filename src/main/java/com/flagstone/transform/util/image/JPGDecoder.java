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
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.image.DefineJPEGImage2;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.image.JPEGInfo;

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
    @Override
	public void read(final File file) throws IOException, DataFormatException {
        read(new FileInputStream(file));
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
	public ImageTag defineImage(final int identifier) {
        return new DefineJPEGImage2(identifier, image);
    }

    /** {@inheritDoc} */
    @Override
	public ImageDecoder newDecoder() {
        return new JPGDecoder();
    }

    /** {@inheritDoc} */
     @Override
	public void read(final InputStream stream)
                 throws DataFormatException, IOException {

         final BigDecoder coder = new BigDecoder(stream);

         int marker;
         int length;

         do {
             marker = coder.readUnsignedShort();
             switch (marker) {
                 case JPEGInfo.SOI:
                 case JPEGInfo.EOI:
                     copyTag(marker, 0, coder);
                     break;
                 case JPEGInfo.SOF0:
                 case JPEGInfo.SOF2:
                 case JPEGInfo.DHT:
                 case JPEGInfo.DQT:
                 case JPEGInfo.COM:
                     length = coder.readUnsignedShort();
                     copyTag(marker, length, coder);
                     break;
                 case JPEGInfo.SOS:
                     length = coder.readUnsignedShort();
                     copyTag(marker, length, coder);
                     readEntropyData(coder);
                     break;
                 case JPEGInfo.DRI:
                     copyTag(marker, 0, coder);
                     readEntropyData(coder);
                     break;
                 default:
                     if ((marker & JPEGInfo.APP) == JPEGInfo.APP) {
                         length = coder.readUnsignedShort();
                         copyTag(marker, length, coder);
                     } else {
                         copyTag(marker, 0, coder);
                     }
                     break;
             }
         } while (marker != JPEGInfo.EOI);

         final JPEGInfo info = new JPEGInfo();
         info.decode(image);
         width = info.getWidth();
         height = info.getHeight();
    }

    /**
     * Copy a JPEG tag.
     * @param marker the identifier for the tag.
     * @param length the length of the tag data.
     * @param coder the decoder containing the JPEG image data.
     * @throws IOException if there is an error reading the image data.
     */
    private void copyTag(final int marker, final int length,
            final BigDecoder coder) throws IOException {
        byte[] bytes;
        if (length > 0) {
            bytes = new byte[length + 2];
        } else {
            bytes = new byte[2];
        }
        bytes[0] = (byte) (marker >> Coder.TO_LOWER_BYTE);
        bytes[1] = (byte) marker;

        if (length > 0) {
            bytes[2] = (byte) (length >> Coder.TO_LOWER_BYTE);
            bytes[3] = (byte) length;
            coder.readBytes(bytes, 4, length - 2);
        }
        final int imgLength = image.length;
        image = Arrays.copyOf(image, imgLength + bytes.length);
        System.arraycopy(bytes, 0, image, imgLength, bytes.length);
    }

    /**
     * Read the encoded image data from a JPEG image.
     * @param coder the decoder containing the JPEG image data.
     * @throws IOException if there is an error reading the image data.
     */
    private void readEntropyData(final BigDecoder coder) throws IOException {
        byte[] bytes = new byte[2048];
        int index = 0;
        int current;
        int next;

        do {
        	coder.mark();
        	current = coder.readByte();

        	if (current == 255) {
        		next = coder.readByte();

        		if (next == 0) {
            		if (index + 2 >= bytes.length) {
                        final int imgLength = image.length;
                        image = Arrays.copyOf(image, imgLength + index);
                        System.arraycopy(bytes, 0, image, imgLength, index);
                        index = 0;
            		}
            		bytes[index++] = (byte) current;
            		bytes[index++] = (byte) next;
        		} else {
            		if (index > 0) {
                        final int imgLength = image.length;
                        image = Arrays.copyOf(image, imgLength + index);
                        System.arraycopy(bytes, 0, image, imgLength, index);
                        index = 0;
            		}
        			coder.reset();
        			break;
        		}
        	} else {
        		if (index >= bytes.length) {
                    final int imgLength = image.length;
                    image = Arrays.copyOf(image, imgLength + bytes.length);
                    System.arraycopy(bytes, 0, image, imgLength, bytes.length);
                    index = 0;
        		}
        		bytes[index++] = (byte) current;
        	}
        	coder.unmark();

        } while (true);
    }

    /** {@inheritDoc} */
    @Override
	public int getWidth() {
        return width;
    }

    /** {@inheritDoc} */
    @Override
	public int getHeight() {
        return height;
    }

    /** {@inheritDoc} */
    @Override
	public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }
}
