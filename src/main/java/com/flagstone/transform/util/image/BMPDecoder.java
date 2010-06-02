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
    /** Mask for reading unsigned 8-bit values. */
    private static final int UNSIGNED_BYTE = 255;
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

    /** Size of each colour table entry or pixel in a true colour image. */
    private static final int COLOUR_CHANNELS = 4;
    /** Size of a pixel in a RGB555 true colour image. */
    private static final int RGB555_SIZE = 16;
    /** Size of a pixel in a RGB8 true colour image. */
    private static final int RGB8_SIZE = 24;

    /** Byte offset to red channel. */
    private static final int RED = 0;
    /** Byte offset to red channel. */
    private static final int GREEN = 1;
    /** Byte offset to blue channel. */
    private static final int BLUE = 2;
    /** Byte offset to alpha channel. */
    private static final int ALPHA = 3;

    /** Mask used to extract red channel from a 16-bit RGB555 pixel. */
    private static final int RGB555_RED_MASK = 0x7C00;
    /** Mask used to extract green channel from a 16-bit RGB555 pixel. */
    private static final int RGB555_GREEN_MASK = 0x03E0;
    /** Mask used to extract blue channel from a 16-bit RGB555 pixel. */
    private static final int RGB555_BLUE_MASK = 0x001F;
    /** Shift used to align the RGB555 red channel to a 8-bit pixel. */
    private static final int RGB555_RED_SHIFT = 7;
    /** Shift used to align the RGB555 green channel to a 8-bit pixel. */
    private static final int RGB555_GREEN_SHIFT = 2;
    /** Shift used to align the RGB555 blue channel to a 8-bit pixel. */
    private static final int RGB555_BLUE_SHIFT = 3;
    /** Mask used to extract red channel from a 16-bit RGB565 pixel. */
    private static final int RGB565_RED_MASK = 0x7C00;
    /** Mask used to extract green channel from a 16-bit RGB565 pixel. */
    private static final int RGB565_GREEN_MASK = 0x03E0;
    /** Mask used to extract blue channel from a 16-bit RGB565 pixel. */
    private static final int RGB565_BLUE_MASK = 0x001F;
    /** Shift used to align the RGB565 red channel to a 8-bit pixel. */
    private static final int RGB565_RED_SHIFT = 8;
    /** Shift used to align the RGB565 green channel to a 8-bit pixel. */
    private static final int RGB565_GREEN_SHIFT = 3;
    /** Shift used to align the RGB565 blue channel to a 8-bit pixel. */
    private static final int RGB565_BLUE_SHIFT = 3;

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

        switch (format) {
        case IDX8:
            object = new DefineImage(identifier, width, height,
                    table.length / COLOUR_CHANNELS,
                    zip(merge(adjustScan(width, height, image), table)));
            break;
        case IDXA:
            object = new DefineImage2(identifier, width, height,
                    table.length / COLOUR_CHANNELS,
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

        coder.readUI32(); // fileSize
        coder.readUI32(); // reserved

        final int offset = coder.readUI32();
        final int headerSize = coder.readUI32();

        int bitsPerPixel;
        int coloursUsed;

        switch (headerSize) {
        case 12:
            width = coder.readUI16();
            height = coder.readUI16();
            coder.readUI16(); // bitPlanes
            bitsPerPixel = coder.readUI16();
            coloursUsed = 0;
            break;
        case 40:
            width = coder.readUI32();
            height = coder.readUI32();
            coder.readUI16(); // bitPlanes
            bitsPerPixel = coder.readUI16();
            compressionMethod = coder.readUI32();
            coder.readUI32(); // imageSize
            coder.readUI32(); // horizontalResolution
            coder.readUI32(); // verticalResolution
            coloursUsed = coder.readUI32();
            coder.readUI32(); // importantColours
            break;
        default:
            bitsPerPixel = 0;
            coloursUsed = 0;
            break;
        }

        if (compressionMethod == BI_BITFIELDS) {
            redMask = coder.readUI32();
            greenMask = coder.readUI32();
            blueMask = coder.readUI32();

            if (redMask == RGB555_RED_MASK) {
                redShift = RGB555_RED_SHIFT;
            } else if (redMask == RGB565_RED_MASK) {
                redShift = RGB565_RED_SHIFT;
            }

            if (greenMask == RGB555_GREEN_MASK) {
                greenShift = RGB555_GREEN_SHIFT;
            } else if (greenMask == RGB565_GREEN_MASK) {
                greenShift = RGB565_GREEN_SHIFT;
            }

            if (blueMask == RGB555_BLUE_MASK) {
                blueShift = RGB555_BLUE_SHIFT;
            } else if (blueMask == RGB565_BLUE_MASK) {
                blueShift = RGB565_BLUE_SHIFT;
            }
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
            table = new byte[coloursUsed * COLOUR_CHANNELS];
            image = new byte[height * width];

            int index = 0;

            if (headerSize == 12) {
                for (int i = 0; i < coloursUsed; i++) {
                    table[index + ALPHA] = (byte) OPAQUE;
                    table[index + BLUE] = (byte) coder.readByte();
                    table[index + GREEN] = (byte) coder.readByte();
                    table[index + RED] = (byte) coder.readByte();
                    index += COLOUR_CHANNELS;
                }
            } else {
                for (int i = 0; i < coloursUsed; i++) {
                    table[index + RED] = (byte) coder.readByte();
                    table[index + GREEN] = (byte) coder.readByte();
                    table[index + BLUE] = (byte) coder.readByte();
                    table[index + ALPHA] = (byte) coder.readByte();
                    index += COLOUR_CHANNELS;
                }
            }

            coder.skip(offset - coder.bytesRead());

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
            image = new byte[height * width * COLOUR_CHANNELS];

            coder.skip(offset - coder.bytesRead());

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

    /**
     * Decode the indexed image data block (IDX8).
     * @param coder the decoder containing the image data.
     */
    private void decodeIDX8(final LittleDecoder coder) throws IOException {
        int index = 0;

        for (int row = height - 1; row > 0; row--) {
            coder.mark();
            index = row * width;

            for (int col = 0; col < width; col++) {
                image[index++] = (byte) coder.readBits(bitDepth, false);
            }
            coder.alignToWord();
            coder.unmark();
        }
    }

    /**
     * Decode the run length encoded image data block (RLE4).
     * @param coder the decoder containing the image data.
     */
    private void decodeRLE4(final LittleDecoder coder) throws IOException {
        int row = height - 1;
        int col = 0;
        int index = 0;
        int value;

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
                    col += coder.readUI16();
                    row -= coder.readUI16();
                    index = row * width + col;
                    for (int i = 0; i < code; i += 2) {
                        value = coder.readByte();
                        image[index++] = (byte) (value >>> 4);
                        image[index++] = (byte) (value & 0x0F);
                    }

                    if ((code & 2) == 2) {
                        coder.readByte();
                    }
                    break;
                default:
                    index = row * width + col;
                    for (int i = 0; i < code; i += 2) {
                        value = coder.readByte();
                        image[index++] = (byte) (value >>> 4);
                        image[index++] = (byte) (value & 0x0F);
                    }

                    if ((code & 2) == 2) {
                        coder.readByte();
                    }
                    break;
                }
            } else {
                value = coder.readByte();
                final byte indexA = (byte) (value >>> 4);
                final byte indexB = (byte) (value & 0x0F);
                index = row * width + col;

                for (int i = 0; (i < count) && (col < width); i++, col++) {
                    image[index++] = (i % 2 > 0) ? indexB : indexA;
                }
            }
        }
    }

    /**
     * Decode the run length encoded image data block (RLE8).
     * @param coder the decoder containing the image data.
     */
    private void decodeRLE8(final LittleDecoder coder) throws IOException {
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
                    col += coder.readUI16();
                    row -= coder.readUI16();
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

    /**
     * Decode the true colour image with each colour channel taking 5-bits.
     * @param coder the decoder containing the image data.
     */
    private void decodeRGB5(final LittleDecoder coder) throws IOException {
        int index = 0;
        int colour;

        for (int row = height - 1; row > 0; row--) {
            coder.mark();
            for (int col = 0; col < width; col++) {
                colour = coder.readUI16();
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
     */
    private void decodeRGB8(final LittleDecoder coder) throws IOException {
        int index = 0;
        for (int row = height - 1; row > 0; row--) {
            coder.mark();
            for (int col = 0; col < width; col++) {
                image[index + RED] = (byte) coder.readByte();
                image[index + GREEN] = (byte) coder.readByte();
                image[index + BLUE] = (byte) coder.readByte();
                image[index + ALPHA] = (byte) OPAQUE;
                index += COLOUR_CHANNELS;
            }
            coder.alignToWord();
            coder.unmark();
        }
    }

    /**
     * Decode the true colour image with each colour channel and alpha taking
     * 8-bits.
     * @param coder the decoder containing the image data.
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
     * Reorder the image pixels from RGBA to ARGB.
     *
     * @param img the image data.
     */
    private void orderAlpha(final byte[] img) {
        byte alpha;

        for (int i = 0; i < img.length; i += COLOUR_CHANNELS) {
            alpha = img[i + ALPHA];
            img[i + ALPHA] = img[i + BLUE];
            img[i + BLUE] = img[i + GREEN];
            img[i + GREEN] = img[i + RED];
            img[i + RED] = alpha;
        }
    }

    /**
     * Apply the level for the alpha channel to the red, green and blue colour
     * channels for encoding the image so it can be added to a Flash movie.
     * @param img the image data.
     */
   private void applyAlpha(final byte[] img) {
        int alpha;

        for (int i = 0; i < img.length; i += COLOUR_CHANNELS) {
            alpha = img[i + ALPHA] & UNSIGNED_BYTE;

            img[i + RED] = (byte) (((img[i + RED] & UNSIGNED_BYTE)
                    * alpha) / OPAQUE);
            img[i + GREEN] = (byte) (((img[i + GREEN] & UNSIGNED_BYTE)
                    * alpha) / OPAQUE);
            img[i + BLUE] = (byte) (((img[i + BLUE] & UNSIGNED_BYTE)
                    * alpha) / OPAQUE);
        }
    }

   /**
    * Concatenate the colour table and the image data together.
    * @param img the image data.
    * @param colors the colour table.
    * @return a single array containing the red, green and blue (not alpha)
    * entries from the colour table followed by the red, green, blue and
    * alpha channels from the image. The alpha defaults to 255 for an opaque
    * image.
    */
    private byte[] merge(final byte[] img, final byte[] colors) {
        final int entries = colors.length / COLOUR_CHANNELS;
        final byte[] merged = new byte[entries * 3 + img.length];
        int dst = 0;

        // Remap RGBA colours from table to BGR in encoded image
        for (int i = 0; i < colors.length; i += COLOUR_CHANNELS) {
            merged[dst++] = colors[i + BLUE];
            merged[dst++] = colors[i + GREEN];
            merged[dst++] = colors[i + RED];
        }

        for (final byte element : img) {
            merged[dst++] = element;
        }

        return merged;
    }

    /**
     * Concatenate the colour table and the image data together.
     * @param img the image data.
     * @param colors the colour table.
     * @return a single array containing entries from the colour table followed
     * by the image.
     */
    private byte[] mergeAlpha(final byte[] img, final byte[] colors) {
        final byte[] merged = new byte[colors.length + img.length];
        int dst = 0;

        for (final byte element : colors) {
            merged[dst++] = element;
        }

        for (final byte element : img) {
            merged[dst++] = element;
        }
        return merged;
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

    /**
     * Adjust the width of each row in an image so the data is aligned to a
     * 16-bit word boundary when loaded in memory. The additional bytes are
     * all set to zero and will not be displayed in the image.
     *
     * @param imgWidth the width of the image in pixels.
     * @param imgHeight the height of the image in pixels.
     * @param img the image data.
     * @return the image data with each row aligned to a 16-bit boundary.
     */
    private byte[] adjustScan(final int imgWidth, final int imgHeight,
            final byte[] img) {
        int src = 0;
        int dst = 0;
        int row;
        int col;

        int scan = 0;
        byte[] formattedImage = null;

        scan = (imgWidth + 3) & ~3;
        formattedImage = new byte[scan * imgHeight];

        for (row = 0; row < imgHeight; row++) {
            for (col = 0; col < imgWidth; col++) {
                formattedImage[dst++] = img[src++];
            }

            while (col++ < scan) {
                formattedImage[dst++] = 0;
            }
        }

        return formattedImage;
    }

    /**
     * Convert an image with 32-bits for the red, green, blue and alpha channels
     * to one where the channels each take 5-bits in a 16-bit word.
     * @param imgWidth the width of the image in pixels.
     * @param imgHeight the height of the image in pixels.
     * @param img the image data.
     * @return the image data with the red, green and blue channels packed into
     * 16-bit words. Alpha is discarded.
     */
    private byte[] packColours(final int imgWidth, final int imgHeight,
            final byte[] img) {
        int src = 0;
        int dst = 0;
        int row;
        int col;

        final int scan = imgWidth + (imgWidth & 1);
        final byte[] formattedImage = new byte[scan * imgHeight * 2];

        for (row = 0; row < imgHeight; row++) {
            for (col = 0; col < imgWidth; col++, src++) {
                final int red = (img[src++] & 0xF8) << 7;
                final int green = (img[src++] & 0xF8) << 2;
                final int blue = (img[src++] & 0xF8) >> 3;
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
