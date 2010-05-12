/*
 * BMPDecoder.java
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;


import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.ImageFormat;

/**
 * BMPDecoder decodes Bitmap images (BMP) so they can be used in a Flash file.
 */
//TODO(class)
@SuppressWarnings("PMD.TooManyMethods")
public final class BMPDecoder implements ImageProvider, ImageDecoder {

    private static final String BAD_FORMAT = "Unsupported Format";

    private static final int[] SIGNATURE = {66, 77};

    private static final int BI_RGB = 0;
    private static final int BI_RLE8 = 1;
    private static final int BI_RLE4 = 2;
    private static final int BI_BITFIELDS = 3;

    private transient ImageFormat format;
    private transient int width;
    private transient int height;
    private transient byte[] table;
    private transient byte[] image;

    private transient int bitDepth;
    private transient int compressionMethod;
    private transient int redMask;
    private transient int greenMask;
    private transient int blueMask;

    /** TODO(method). */
    public void read(final File file) throws IOException, DataFormatException {
        read(new FileInputStream(file), (int) file.length());
    }

    /** TODO(method). */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();

        if (!connection.getContentType().equals("image/bmp")) {
            throw new DataFormatException(BAD_FORMAT);
        }

        final int length = connection.getContentLength();

        if (length < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        read(url.openStream(), length);
    }

    /** TODO(method). */
    public ImageTag defineImage(final int identifier) {
        ImageTag object = null;

        switch (format) {
        case IDX8:
            object = new DefineImage(identifier, width, height, table.length/4,
                    zip(merge(adjustScan(width, height, image), table)));
            break;
        case IDXA:
            object = new DefineImage2(identifier, width, height, table.length/4,
                    zip(mergeAlpha(adjustScan(width, height, image), table)));
            break;
        case RGB5:
            object = new DefineImage(identifier, width, height,
                    zip(packColours(width, height, image)), 16);
            break;
        case RGB8:
            orderAlpha(image);
            object = new DefineImage(identifier, width, height, zip(image), 24);
            break;
        case RGBA:
            applyAlpha(image);
            object = new DefineImage2(identifier, width, height, zip(image));
            break;
        default:
            throw new AssertionError(BAD_FORMAT);
        }
        return object;
    }

    /** TODO(method). */
    public ImageDecoder newDecoder() {
        return new BMPDecoder();
    }

    /** TODO(method). */
    public int getWidth() {
        return width;
    }

    /** TODO(method). */
    public int getHeight() {
        return height;
    }

    /** TODO(method). */
    public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }

    /** TODO(method). */
    public void read(final InputStream stream, final int length) throws DataFormatException, IOException {

        final byte[] bytes = new byte[(int) length];
        final BufferedInputStream buffer = new BufferedInputStream(stream);

        buffer.read(bytes);
        buffer.close();

        final SWFDecoder coder = new SWFDecoder(bytes);

        for (int i = 0; i < 2; i++) {
            if (coder.readByte() != SIGNATURE[i]) {
                throw new DataFormatException(BAD_FORMAT);
            }
        }

        coder.readWord(4, false); // fileSize
        coder.readWord(4, false); // reserved

        final int offset = coder.readWord(4, false);
        final int headerSize = coder.readWord(4, false);

        int bitsPerPixel;
        int coloursUsed;

        switch (headerSize) {
        case 12:
            width = coder.readWord(2, false);
            height = coder.readWord(2, false);
            coder.readWord(2, false); // bitPlanes
            bitsPerPixel = coder.readWord(2, false);
            coloursUsed = 0;
            break;
        case 40:
            width = coder.readWord(4, false);
            height = coder.readWord(4, false);
            coder.readWord(2, false); // bitPlanes
            bitsPerPixel = coder.readWord(2, false);
            compressionMethod = coder.readWord(4, false);
            coder.readWord(4, false); // imageSize
            coder.readWord(4, false); // horizontalResolution
            coder.readWord(4, false); // verticalResolution
            coloursUsed = coder.readWord(4, false);
            coder.readWord(4, false); // importantColours
            break;
        default:
            bitsPerPixel = 0;
            coloursUsed = 0;
            break;
        }

        if (compressionMethod == BI_BITFIELDS) {
            redMask = coder.readWord(4, false);
            greenMask = coder.readWord(4, false);
            blueMask = coder.readWord(4, false);
        }

        switch (bitsPerPixel) {
        case 1:
            format = ImageFormat.IDX8;
            bitDepth = 1;
            break;
        case 2:
            format = ImageFormat.IDX8;
            bitDepth = 2;
            break;
        case 4:
            format = ImageFormat.IDX8;
            bitDepth = 4;
            break;
        case 8:
            format = ImageFormat.IDX8;
            bitDepth = 8;
            break;
        case 16:
            format = ImageFormat.RGB5;
            bitDepth = 5;
            break;
        case 24:
            format = ImageFormat.RGB8;
            bitDepth = 8;
            break;
        case 32:
            format = ImageFormat.RGBA;
            bitDepth = 8;
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }

        if (format == ImageFormat.IDX8) {
            coloursUsed = 1 << bitsPerPixel;
            table = new byte[coloursUsed * 4];
            image = new byte[height * width];

            int index = 0;

            if (headerSize == 12) {
                for (int i = 0; i < coloursUsed; i++, index += 4) {
                    table[index + 3] = (byte) 0xFF;
                    table[index + 2] = (byte) coder.readByte();
                    table[index + 1] = (byte) coder.readByte();
                    table[index] = (byte) coder.readByte();
                }
            } else {
                for (int i = 0; i < coloursUsed; i++, index += 4) {
                    table[index] = (byte) coder.readByte();
                    table[index + 1] = (byte) coder.readByte();
                    table[index + 2] = (byte) coder.readByte();
                    table[index + 3] = (byte) (coder.readByte() | 0xFF);
                }
            }

            coder.setPointer(offset << 3);

            switch (compressionMethod) {
            case BI_RGB:
                decodeIDX8(coder);
                break;
            case BI_RLE8:
                decodeRLE8(coder);
                break;
            case BI_RLE4:
                decodeRLE4(coder);
                break;
            default:
                throw new DataFormatException(BAD_FORMAT);
            }
        } else {
            image = new byte[height * width * 4];

            coder.setPointer(offset << 3);

            switch (format) {
            case RGB5:
                decodeRGB5(coder);
                break;
            case RGB8:
                decodeRGB8(coder);
                break;
            case RGBA:
                decodeRGBA(coder);
                break;
            default:
                throw new DataFormatException(BAD_FORMAT);
            }
        }
    }

    private void decodeIDX8(final SWFDecoder coder) {
        int bitsRead;
        int index = 0;

        for (int row = height - 1; row > 0; row--) {
            bitsRead = 0;
            index = row * width;
            
            for (int col = 0; col < width; col++) {
                image[index++] = (byte) coder.readBits(bitDepth, false);
                bitsRead += bitDepth;
            }

            if (bitsRead % 32 > 0) {
                coder.adjustPointer(32 - (bitsRead % 32));
            }
        }
    }

    private void decodeRLE4(final SWFDecoder coder) {
        int row = height - 1;
        int col = 0;
        int index = 0;

        boolean hasMore = true;

        while (hasMore) {
            final int count = coder.readByte();

            if (count == 0) {
                final int code = coder.readByte();

                switch (code) {
                case 0:
                    col = 0;
                    row--;
                    break;
                case 1:
                    hasMore = false;
                    break;
                case 2:
                    col += coder.readWord(2, false);
                    row -= coder.readWord(2, false);
                    index = row * width + col;
                    
                    for (int i = 0; i < code; i += 2) {
                        image[index++] = (byte) coder.readBits(4, false);
                        image[index++] = (byte) coder.readBits(4, false);
                    }

                    if ((code & 2) == 2) {
                        coder.readByte();
                    }
                    break;
                default:
                    index = row * width + col;
                    for (int i = 0; i < code; i += 2) {
                        image[index++] = (byte) coder.readBits(4, false);
                        image[index++] = (byte) coder.readBits(4, false);
                    }

                    if ((code & 2) == 2) {
                        coder.readByte();
                    }
                    break;
                }
            } else {
                final byte indexA = (byte) coder.readBits(4, false);
                final byte indexB = (byte) coder.readBits(4, false);
                index = row * width + col;
                
                for (int i = 0; (i < count) && (col < width); i++, col++) {
                    image[index++] = (i % 2 > 0) ? indexB : indexA;
                }
            }
        }
    }

    private void decodeRLE8(final SWFDecoder coder) {
        int row = height - 1;
        int col = 0;
        int index = 0;

        boolean hasMore = true;

        while (hasMore) {
            final int count = coder.readByte();

            if (count == 0) {
                final int code = coder.readByte();

                switch (code) {
                case 0:
                    col = 0;
                    row--;
                    break;
                case 1:
                    hasMore = false;
                    break;
                case 2:
                    col += coder.readWord(2, false);
                    row -= coder.readWord(2, false);
                    index = row * width + col;
                    for (int i = 0; i < code; i++) {
                        image[index++] = (byte) coder.readByte();
                    }

                    if ((code & 1) == 1) {
                        coder.readByte();
                    }
                    break;
                default:
                    index = row * width + col;
                    for (int i = 0; i < code; i++) {
                        image[index++] = (byte) coder.readByte();
                    }

                    if ((code & 1) == 1) {
                        coder.readByte();
                    }
                    break;
                }
            } else {
                final byte value = (byte) coder.readByte();
                index = row * width + col;

                for (int i = 0; i < count; i++) {
                    image[index++] = value;
                }
            }
        }
    }

    private void decodeRGB5(final SWFDecoder coder) {
        int bitsRead = 0;
        int index = 0;

        if (compressionMethod == BI_RGB) {
            for (int row = height - 1; row > 0; row--) {
                bitsRead = 0;

                for (int col = 0; col < width; col++, index += 4) {
                    final int colour = coder.readWord(2, false) & 0xFFFF;

                    image[index] = (byte) ((colour & 0x7C00) >> 7);
                    image[index + 1] = (byte) ((colour & 0x03E0) >> 2);
                    image[index + 2] = (byte) ((colour & 0x001F) << 3);
                    image[index + 3] = (byte) 0xFF;

                    bitsRead += 16;
                }
                if (bitsRead % 32 > 0) {
                    coder.adjustPointer(32 - (bitsRead % 32));
                }
            }
        } else {
            for (int row = height - 1; row > 0; row--) {
                bitsRead = 0;

                for (int col = 0; col < width; col++, index += 4) {
                    final int colour = coder.readWord(2, false) & 0xFFFF;

                    if ((redMask == 0x7C00) && (greenMask == 0x03E0)
                            && (blueMask == 0x001F)) {
                        image[index] = (byte) ((colour & 0x7C00) >> 7);
                        image[index + 1] = (byte) ((colour & 0x03E0) >> 2);
                        image[index + 2] = (byte) ((colour & 0x001F) << 3);
                        image[index + 3] = (byte) 0xFF;
                    } else if ((redMask == 0xF800) && (greenMask == 0x07E0)
                            && (blueMask == 0x001F)) {
                        image[index] = (byte) ((colour & 0xF800) >> 8);
                        image[index + 1] = (byte) ((colour & 0x07E0) >> 3);
                        image[index + 2] = (byte) ((colour & 0x001F) << 3);
                        image[index + 3] = (byte) 0xFF;
                    }
                    bitsRead += 16;
                }
                if (bitsRead % 32 > 0) {
                    coder.adjustPointer(32 - (bitsRead % 32));
                }
            }
        }

    }

    private void decodeRGB8(final SWFDecoder coder) {
        int bitsRead;
        int index = 0;

        for (int row = height - 1; row > 0; row--) {
            bitsRead = 0;

            for (int col = 0; col < width; col++, index += 4) {
                image[index] = (byte) coder.readBits(bitDepth, false);
                image[index + 1] = (byte) coder.readBits(bitDepth, false);
                image[index + 2] = (byte) coder.readBits(bitDepth, false);
                image[index + 3] = (byte) 0xFF;

                bitsRead += 24;
            }

            if (bitsRead % 32 > 0) {
                coder.adjustPointer(32 - (bitsRead % 32));
            }
        }
    }

    private void decodeRGBA(final SWFDecoder coder) {
        int index = 0;

        for (int row = height - 1; row > 0; row--) {
            for (int col = 0; col < width; col++, index += 4) {
                image[index + 2] = (byte) coder.readByte();
                image[index + 1] = (byte) coder.readByte();
                image[index] = (byte) coder.readByte();
                image[index + 3] = (byte) coder.readByte();
                image[index + 3] = (byte) 0xFF;
            }
        }
    }

    private void orderAlpha(final byte[] image) {
        byte alpha;

        for (int i = 0; i < image.length; i += 4) {
            alpha = image[i + 3];

            image[i + 3] = image[i + 2];
            image[i + 2] = image[i + 1];
            image[i + 1] = image[i];
            image[i] = alpha;
        }
    }

    private void applyAlpha(final byte[] image) {
        int alpha;

        for (int i = 0; i < image.length; i += 4) {
            alpha = image[i + 3] & 0xFF;

            image[i] = (byte) (((image[i] & 0xFF) * alpha) / 255);
            image[i + 1] = (byte) (((image[i + 1] & 0xFF) * alpha) / 255);
            image[i + 2] = (byte) (((image[i + 2] & 0xFf) * alpha) / 255);
        }
    }

    private byte[] merge(final byte[] image, final byte[] table) {
        final byte[] merged = new byte[(table.length / 4) * 3 + image.length];
        int dst = 0;

        for (int i = 0; i < table.length; i += 4) {
            merged[dst++] = table[i + 2]; // R
            merged[dst++] = table[i + 1]; // G
            merged[dst++] = table[i]; // B
        }

        for (final byte element : image) {
            merged[dst++] = element;
        }

        return merged;
    }

    private byte[] mergeAlpha(final byte[] image, final byte[] table) {
        final byte[] merged = new byte[table.length + image.length];
        int dst = 0;

        for (final byte element : table) {
            merged[dst++] = element;
        }

        for (final byte element : image) {
            merged[dst++] = element;
        }
        return merged;
    }

    private byte[] zip(final byte[] image) {
        final Deflater deflater = new Deflater();
        deflater.setInput(image);
        deflater.finish();

        final byte[] compressedData = new byte[image.length * 2];
        final int bytesCompressed = deflater.deflate(compressedData);
        final byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);

        return newData;
    }

    private byte[] adjustScan(final int width, final int height,
            final byte[] image) {
        int src = 0;
        int dst = 0;
        int row;
        int col;

        int scan = 0;
        byte[] formattedImage = null;

        scan = (width + 3) & ~3;
        formattedImage = new byte[scan * height];

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                formattedImage[dst++] = image[src++];
            }

            while (col++ < scan) {
                formattedImage[dst++] = 0;
            }
        }

        return formattedImage;
    }

    private byte[] packColours(final int width, final int height,
            final byte[] image) {
        int src = 0;
        int dst = 0;
        int row;
        int col;

        final int scan = width + (width & 1);
        final byte[] formattedImage = new byte[scan * height * 2];

        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++, src++) {
                final int red = (image[src++] & 0xF8) << 7;
                final int green = (image[src++] & 0xF8) << 2;
                final int blue = (image[src++] & 0xF8) >> 3;
                final int colour = (red | green | blue) & 0x7FFF;

                formattedImage[dst++] = (byte) (colour >> 8);
                formattedImage[dst++] = (byte) colour;
            }

            while (col < scan) {
                formattedImage[dst++] = 0;
                formattedImage[dst++] = 0;
                col++;
            }
        }
        return formattedImage;
    }

}
