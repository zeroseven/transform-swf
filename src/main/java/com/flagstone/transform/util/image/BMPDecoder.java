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

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.LittleDecoder;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.ImageFormat;
import com.flagstone.transform.image.ImageTag;

/**
 * BMPDecoder decodes Bitmap images (BMP) so they can be used in a Flash file.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class BMPDecoder implements ImageProvider, ImageDecoder {

    /** Level used to indicate an opaque colour. */
    private static final int OPAQUE = 255;
    /** Message used to signal that the image cannot be decoded. */
    private static final String BAD_FORMAT = "Unsupported Format";
    /** The signature identifying BMP files. */
    private static final int[] SIGNATURE = {66, 77};
    /** An uncompressed indexed image. */
    private static final int BI_RGB = 0;
    /** A run-length compressed image with 8 bits per pixel. */
    private static final int BI_RLE8 = 1;
    /** A run-length compressed image with 4 bits per pixel. */
    private static final int BI_RLE4 = 2;
    /** A true-colour image. */
    private static final int BI_BITFIELDS = 3;

    /** The size of the header for an uncompressed image. */
    private static final int UNZIPPED_LENGTH = 12;
    /** The size of the header for an compressed image. */
    private static final int ZIPPED_LENGTH  = 40;

    /** Size of each colour table entry or pixel in a true colour image. */
    private static final int COLOUR_CHANNELS = 4;
    /** Size of a pixel in an indexed image with 1 bit per pixel. */
    private static final int IDX_1 = 1;
    /** Size of a pixel in an indexed image with 2 bits per pixel. */
    private static final int IDX_2 = 2;
    /** Size of a pixel in an indexed image with 4 bits per pixel. */
    private static final int IDX_4 = 4;
    /** Size of a pixel in an indexed image with 8 bits per pixel. */
    private static final int IDX_8 = 8;
   /** Size of a pixel in a RGB555 true colour image. */
    private static final int RGB5_SIZE = 16;
    /** Size of a pixel in a RGB8 true colour image. */
    private static final int RGB8_SIZE = 24;
    /** Size of a pixel in a RGB8 true colour image. */
    private static final int RGBA_SIZE = 32;

    /** Number of bits for each colour channel in a RGB555 pixel. */
    private static final int RGB5_DEPTH = 5;
    /** Number of bits for each colour channel in a RGB8 pixel. */
    private static final int RGB8_DEPTH = 8;

    /** Byte offset to red channel. */
    private static final int RED = 0;
    /** Byte offset to red channel. */
    private static final int GREEN = 1;
    /** Byte offset to blue channel. */
    private static final int BLUE = 2;
    /** Byte offset to alpha channel. */
    private static final int ALPHA = 3;

    /** Mask used to extract red channel from a 16-bit RGB555 pixel. */
    private static final int R5_MASK = 0x7C00;
    /** Mask used to extract green channel from a 16-bit RGB555 pixel. */
    private static final int G5_MASK = 0x03E0;
    /** Mask used to extract blue channel from a 16-bit RGB555 pixel. */
    private static final int B5_MASK = 0x001F;
    /** Shift used to align the RGB555 red channel to a 8-bit pixel. */
    private static final int R5_SHIFT = 7;
    /** Shift used to align the RGB555 green channel to a 8-bit pixel. */
    private static final int G5_SHIFT = 2;
    /** Shift used to align the RGB555 blue channel to a 8-bit pixel. */
    private static final int B5_SHIFT = 3;
    /** Mask used to extract red channel from a 16-bit RGB565 pixel. */
    private static final int R6_MASK = 0x7C00;
    /** Mask used to extract green channel from a 16-bit RGB565 pixel. */
    private static final int G6_MASK = 0x03E0;
    /** Mask used to extract blue channel from a 16-bit RGB565 pixel. */
    private static final int B6_MASK = 0x001F;
    /** Shift used to align the RGB565 red channel to a 8-bit pixel. */
    private static final int R6_SHIFT = 8;
    /** Shift used to align the RGB565 green channel to a 8-bit pixel. */
    private static final int G6_SHIFT = 3;
    /** Shift used to align the RGB565 blue channel to a 8-bit pixel. */
    private static final int B6_SHIFT = 3;

    /** The format of the decoded image. */
    private transient ImageFormat format;
    /** The width of the image in pixels. */
    private transient int width;
    /** The height of the image in pixels. */
    private transient int height;
    /** The colour table for indexed images. */
    private transient byte[] table;
    /** The image data. */
    private transient byte[] image;

    /** The number of bits per pixel. */
    private transient int bitDepth;
    /** The method used to compress the image. */
    private transient int compressionMethod;
    /** The bit mask used to extract the red channel from the pixel word. */
    private transient int redMask;
    /** Shift for the red pixel to convert to an 8-bit colour. */
    private transient int redShift;
    /** The bit mask used to extract the green channel from the pixel word. */
    private transient int greenMask;
    /** Shift for the green pixel to convert to an 8-bit colour. */
    private transient int greenShift;
    /** The bit mask used to extract the blue channel from the pixel word. */
    private transient int blueMask;
    /** Shift for the blue pixel to convert to an 8-bit colour. */
    private transient int blueShift;
    private transient int bitsPerPixel;
    private transient int coloursUsed;

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
        ImageTag object = null;
        final ImageFilter filter = new ImageFilter();

        switch (format) {
        case IDX8:
            object = new DefineImage(identifier, width, height,
                    table.length / COLOUR_CHANNELS,
                    zip(filter.merge(filter.adjustScan(width, height, image),
                            table)));
            break;
        case IDXA:
            object = new DefineImage2(identifier, width, height,
                    table.length / COLOUR_CHANNELS,
                    zip(filter.mergeAlpha(
                            filter.adjustScan(width, height, image), table)));
            break;
        case RGB5:
            object = new DefineImage(identifier, width, height,
                    zip(filter.packColors(width, height, image)), RGB5_SIZE);
            break;
        case RGB8:
            filter.orderAlpha(image);
            object = new DefineImage(identifier, width, height, zip(image),
                    RGB8_SIZE);
            break;
        case RGBA:
            filter.applyAlpha(image);
            object = new DefineImage2(identifier, width, height, zip(image));
            break;
        default:
            throw new AssertionError(BAD_FORMAT);
        }
        return object;
    }

    /** {@inheritDoc} */
    public ImageDecoder newDecoder() {
        return new BMPDecoder();
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

    /** {@inheritDoc} */
    public void read(final InputStream stream)
                    throws DataFormatException, IOException {

        final LittleDecoder coder = new LittleDecoder(stream);
        coder.mark();

        for (int i = 0; i < 2; i++) {
            if (coder.readByte() != SIGNATURE[i]) {
                throw new DataFormatException(BAD_FORMAT);
            }
        }

        coder.readInt(); // fileSize
        coder.readInt(); // reserved

        final int offset = coder.readInt();
        final int headerSize = coder.readInt();

        if (headerSize == ZIPPED_LENGTH) {
            decodeCompressedHeader(coder);
        } else {
            decodeHeader(coder);
        }

        decodeFormat(bitsPerPixel);

        if (format == ImageFormat.IDX8) {
            coloursUsed = 1 << bitsPerPixel;

            if (headerSize == UNZIPPED_LENGTH) {
                decodeTable(coloursUsed, coder);
            } else {
                decodeTableWithAlpha(coloursUsed, coder);
            }

            coder.skip(offset - coder.bytesRead());
            decodeIndexedImage(coder);
        } else {
            coder.skip(offset - coder.bytesRead());
            decodeColourImage(coder);
        }
    }

    private void decodeHeader(final LittleDecoder coder) throws IOException {
        width = coder.readUnsignedShort();
        height = coder.readUnsignedShort();
        coder.readUnsignedShort(); // bitPlanes
        bitsPerPixel = coder.readUnsignedShort();
        coloursUsed = 0;
    }

    private void decodeCompressedHeader(final LittleDecoder coder)
        throws IOException {

        width = coder.readInt();
        height = coder.readInt();
        coder.readUnsignedShort(); // bitPlanes
        bitsPerPixel = coder.readUnsignedShort();
        compressionMethod = coder.readInt();
        coder.readInt(); // imageSize
        coder.readInt(); // horizontalResolution
        coder.readInt(); // verticalResolution
        coloursUsed = coder.readInt();
        coder.readInt(); // importantColours

        if (compressionMethod == BI_BITFIELDS) {
            decodeMasks(coder);
        }
}

    private void decodeMasks(final LittleDecoder coder) throws IOException {
        redMask = coder.readInt();
        greenMask = coder.readInt();
        blueMask = coder.readInt();

        if (redMask == R5_MASK) {
            redShift = R5_SHIFT;
        } else if (redMask == R6_MASK) {
            redShift = R6_SHIFT;
        }

        if (greenMask == G5_MASK) {
            greenShift = G5_SHIFT;
        } else if (greenMask == G6_MASK) {
            greenShift = G6_SHIFT;
        }

        if (blueMask == B5_MASK) {
            blueShift = B5_SHIFT;
        } else if (blueMask == B6_MASK) {
            blueShift = B6_SHIFT;
        }
    }

    private void decodeFormat(final int pixelSize)
                throws DataFormatException {
        switch (pixelSize) {
        case IDX_1:
            format = ImageFormat.IDX8;
            bitDepth = pixelSize;
            break;
        case IDX_2:
            format = ImageFormat.IDX8;
            bitDepth = pixelSize;
            break;
        case IDX_4:
            format = ImageFormat.IDX8;
            bitDepth = pixelSize;
            break;
        case IDX_8:
            format = ImageFormat.IDX8;
            bitDepth = pixelSize;
            break;
        case RGB5_SIZE:
            format = ImageFormat.RGB5;
            bitDepth = RGB5_DEPTH;
            break;
        case RGB8_SIZE:
            format = ImageFormat.RGB8;
            bitDepth = RGB8_DEPTH;
            break;
        case RGBA_SIZE:
            format = ImageFormat.RGBA;
            bitDepth = RGB8_DEPTH;
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }
    }

    private void decodeTable(final int numColours, final LittleDecoder coder)
            throws IOException {
        int index = 0;
        table = new byte[numColours * COLOUR_CHANNELS];

        for (int i = 0; i < numColours; i++) {
            table[index + ALPHA] = (byte) OPAQUE;
            table[index + BLUE] = (byte) coder.readByte();
            table[index + GREEN] = (byte) coder.readByte();
            table[index + RED] = (byte) coder.readByte();
            index += COLOUR_CHANNELS;
        }
    }

    private void decodeTableWithAlpha(final int numColours,
            final LittleDecoder coder) throws IOException {
        int index = 0;
        table = new byte[numColours * COLOUR_CHANNELS];

        for (int i = 0; i < numColours; i++) {
            table[index + RED] = (byte) coder.readByte();
            table[index + GREEN] = (byte) coder.readByte();
            table[index + BLUE] = (byte) coder.readByte();
            table[index + ALPHA] = (byte) coder.readByte();
            index += COLOUR_CHANNELS;
        }
    }

    private void decodeIndexedImage(final LittleDecoder coder)
            throws IOException, DataFormatException {

        image = new byte[height * width];

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
    }

    private void decodeColourImage(final LittleDecoder coder)
            throws IOException, DataFormatException {

        image = new byte[height * width * COLOUR_CHANNELS];

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

    /**
     * Decode the indexed image data block (IDX8).
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeIDX8(final LittleDecoder coder) throws IOException {
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
                coder.readBits(32 - (bitsRead % 32), false);
            }
        }
    }

    /**
     * Decode the run length encoded image data block (RLE4).
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeRLE4(final LittleDecoder coder) throws IOException {
        int row = height - 1;
        int col = 0;
        int index = 0;
        int value;

        boolean hasMore = true;
        int code;
        int count;

        while (hasMore) {
            count = coder.readByte();
            if (count == 0) {
                code = coder.readByte();

                switch (code) {
                case 0:
                    col = 0;
                    row--;
                    break;
                case 1:
                    hasMore = false;
                    break;
                case 2:
                    col += coder.readUnsignedShort();
                    row -= coder.readUnsignedShort();
                    decodeRLE4Pixels(code, coder, row, col);
                    break;
                default:
                    decodeRLE4Pixels(code, coder, row, col);
                    break;
                }
            } else {
                value = coder.readByte();
                final byte indexA = (byte) (value >>> Coder.TO_LOWER_NIB);
                final byte indexB = (byte) (value & Coder.NIB0);
                index = row * width + col;

                for (int i = 0; (i < count) && (col < width); i++, col++) {
                    image[index++] = (i % 2 > 0) ? indexB : indexA;
                }
            }
        }
    }

    private void decodeRLE4Pixels(final int code, final LittleDecoder coder,
            final int row, final int col) throws IOException {
        int index = row * width + col;
        int value;
        for (int i = 0; i < code; i += 2) {
            value = coder.readByte();
            image[index++] = (byte) (value >>> Coder.TO_LOWER_NIB);
            image[index++] = (byte) (value & Coder.NIB0);
        }

        if ((code & 2) == 2) {
            coder.readByte();
        }
    }


    /**
     * Decode the run length encoded image data block (RLE8).
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeRLE8(final LittleDecoder coder) throws IOException {
        int row = height - 1;
        int col = 0;

        boolean hasMore = true;
        int code;
        int count;

        while (hasMore) {
            count = coder.readByte();

            if (count == 0) {
                code = coder.readByte();

                switch (code) {
                case 0:
                    col = 0;
                    row--;
                    break;
                case 1:
                    hasMore = false;
                    break;
                case 2:
                    col += coder.readUnsignedShort();
                    row -= coder.readUnsignedShort();
                    decodeRLE8Pixels(code, coder, row, col);
                    break;
                default:
                    decodeRLE8Pixels(code, coder, row, col);
                    break;
                }
            } else {
                decodeRLE8Run(count, row, col, (byte) coder.readByte());
            }
        }
    }

    private void decodeRLE8Pixels(final int code, final LittleDecoder coder,
            final int row, final int col) throws IOException {
        int index = row * width + col;
        for (int i = 0; i < code; i++) {
            image[index++] = (byte) coder.readByte();
        }

        if ((code & 1) == 1) {
            coder.readByte();
        }
    }

    private void decodeRLE8Run(final int count, final int row, final int col,
            final byte value) {
        int index = row * width + col;

        for (int i = 0; i < count; i++) {
            image[index++] = value;
        }
    }

    /**
     * Decode the true colour image with each colour channel taking 5-bits.
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeRGB5(final LittleDecoder coder) throws IOException {
        int index = 0;
        int colour;

        for (int row = height - 1; row > 0; row--) {
            coder.mark();
            for (int col = 0; col < width; col++) {
                colour = coder.readUnsignedShort();
                image[index + RED] = (byte) ((colour & redMask)
                        >> redShift);
                image[index + GREEN] = (byte) ((colour & greenMask)
                        >> greenShift);
                image[index + BLUE] = (byte) ((colour & blueMask)
                        << blueShift);
                image[index + ALPHA] = (byte) OPAQUE;
                index += COLOUR_CHANNELS;
            }
            coder.alignToWord();
            coder.unmark();
        }
    }

    /**
     * Decode the true colour image with each colour channel taking 8-bits.
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeRGB8(final LittleDecoder coder) throws IOException {
        int bytesRead;
        int index = 0;
        for (int row = height - 1; row > 0; row--) {
            bytesRead = 0;
            for (int col = 0; col < width; col++) {
                image[index + RED] = (byte) coder.readByte();
                image[index + GREEN] = (byte) coder.readByte();
                image[index + BLUE] = (byte) coder.readByte();
                image[index + ALPHA] = (byte) OPAQUE;
                index += COLOUR_CHANNELS;
                bytesRead += 3;
            }
            if (bytesRead % 4 > 0) {
                coder.readBytes(new byte[4 - (bytesRead % 4)]);
            }
        }
    }

    /**
     * Decode the true colour image with each colour channel and alpha taking
     * 8-bits.
     * @param coder the decoder containing the image data.
     * @throws IOException is there is an error decoding the data.
     */
    private void decodeRGBA(final LittleDecoder coder) throws IOException {
        int index = 0;

        for (int row = height - 1; row > 0; row--) {
            for (int col = 0; col < width; col++) {
                image[index + BLUE] = (byte) coder.readByte();
                image[index + GREEN] = (byte) coder.readByte();
                image[index + RED] = (byte) coder.readByte();
                // force alpha channel to be opaque
                image[index + ALPHA] = (byte) coder.readByte();
                image[index + ALPHA] = (byte) OPAQUE;
                index += COLOUR_CHANNELS;
            }
        }
    }

    /**
     * Compress the image using the ZIP format.
     * @param img the image data.
     * @return the compressed image.
     */
    private byte[] zip(final byte[] img) {
        final Deflater deflater = new Deflater();
        deflater.setInput(img);
        deflater.finish();

        final byte[] compressedData = new byte[img.length * 2];
        final int bytesCompressed = deflater.deflate(compressedData);
        final byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);

        return newData;
    }
}
