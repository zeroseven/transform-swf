/*
 * BufferedImageDecoder.java
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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
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
import java.util.zip.Deflater;

import javax.imageio.ImageIO;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.LittleDecoder;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.ImageFormat;
import com.flagstone.transform.image.ImageTag;

/**
 * BufferedImageDecoder decodes BufferedImages so they can be used in a Flash
 * file. The class also provides a set of convenience methods for converting
 * Flash images definitions into BufferedImages allowing the images to easily
 * be extracted from a Flash movie.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class BufferedImageDecoder implements ImageProvider, ImageDecoder {
    /** Message used to signal that the image cannot be decoded. */
    private static final String BAD_FORMAT = "Unsupported format";
    /** The number of bytes per pixel in a RGBA format image. */
    private static final int BYTES_PER_PIXEL = 4;
    /** The alpha channel level for an opaque pixel. */
    private static final int OPAQUE = -1;
    /** Position in 32-bit word of red channel. */
    private static final int RED = 0;
    /** Position in 32-bit word of green channel. */
    private static final int GREEN = 1;
    /** Position in 32-bit word of blue channel. */
    private static final int BLUE = 2;
    /** Position in 32-bit word of alpha channel. */
    private static final int ALPHA = 3;
    /** Mask applied to extract 8-bit values. */
    private static final int MASK_8BIT = 0x00FF;
    /**
     * Number of bits to shift when aligning to the second byte in a 16-bit
     * or 32-bit word. */
    private static final int ALIGN_BYTE2 = 8;
    /**
     * Number of bits to shift when aligning to the second byte in a 16-bit
     * or 32-bit word. */
    private static final int ALIGN_BYTE3 = 16;
    /**
     * Number of bits to shift when aligning to the second byte in a 32-bit
     * word. */
    private static final int ALIGN_BYTE4 = 24;

    /**
     * Value added to offsets to ensure image width is aligned on a 16-bit
     * boundary, 3 == 2 bytes + 1.
     */
    private static final int WORD_ALIGN = 3;

    /** Size of each colour table entry or pixel in a true colour image. */
    private static final int COLOUR_CHANNELS = 4;
    /** Size of each colour table entry or pixel in a RGB image. */
    private static final int RGB_CHANNELS = 3;

    /** Size of a pixel in a RGB555 true colour image. */
    private static final int RGB5_SIZE = 16;
    /** Size of a pixel in a RGB8 true colour image. */
    private static final int RGB8_SIZE = 24;

    /** Shift used to align the RGB555 red channel to a 8-bit pixel. */
    private static final int RGB5_MSB_MASK = 0x00F8;
    /** Shift used to align the RGB555 red channel to a 8-bit pixel. */
    private static final int RGB5_RED_SHIFT = 7;
    /** Shift used to align the RGB555 green channel to a 8-bit pixel. */
    private static final int RGB5_GREEN_SHIFT = 2;
    /** Shift used to align the RGB555 blue channel to a 8-bit pixel. */
    private static final int RGB5_BLUE_SHIFT = 3;


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

    /** {@inheritDoc} */
    public ImageDecoder newDecoder() {
        return new BufferedImageDecoder();
    }

    /** {@inheritDoc} */
    public void read(final File file) throws IOException, DataFormatException {
         read(new FileInputStream(file));
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();
        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        read(url.openStream());
    }

    /** {@inheritDoc} */
    public void read(final InputStream stream)
            throws IOException, DataFormatException {
        read(ImageIO.read(stream));
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
                    zip(packColours(width, height, image)), RGB5_SIZE);
            break;
        case RGB8:
            orderAlpha(image);
            object = new DefineImage(identifier, width, height, zip(image),
                    RGB8_SIZE);
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

    /**
     * Create an image definition from a BufferedImage.
     *
     * @param identifier
     *            the unique identifier that will be used to refer to the image
     *            in the Flash file.
     *
     * @param obj
     *            the BufferedImage containing the image.
     *
     * @return an image definition that can be added to a Movie.
     *
     * @throws DataFormatException
     *             if there is a problem extracting the image, from the
     *             BufferedImage image.
     */
    public ImageTag defineImage(final int identifier, final BufferedImage obj)
            throws IOException, DataFormatException {
        ImageTag object = null;

        final BufferedImageDecoder decoder = new BufferedImageDecoder();
        decoder.read(obj);

        switch (format) {
        case IDX8:
            object = new DefineImage(identifier, width, height, table.length,
                    zip(merge(adjustScan(width, height, image), table)));
            break;
        case IDXA:
            object = new DefineImage2(identifier, width, height, table.length,
                    zip(mergeAlpha(adjustScan(width, height, image), table)));
            break;
        case RGB5:
            object = new DefineImage(identifier, width, height,
                    zip(packColours(width, height, image)), RGB5_SIZE);
            break;
        case RGB8:
            orderAlpha(image);
            object = new DefineImage(identifier, width, height, zip(image),
                    RGB8_SIZE);
            break;
        case RGBA:
            applyAlpha(image);
            object = new DefineImage2(identifier, width, height, zip(image));
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }
        return object;
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
     * Decode a BufferedImage.
     *
     * @param obj
     *            a BufferedImage.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the BufferedImage.
     */
    public void read(final BufferedImage obj) throws IOException, DataFormatException {

        final DataBuffer buffer = obj.getData().getDataBuffer();

        width = obj.getWidth();
        height = obj.getHeight();

        if (buffer.getDataType() == DataBuffer.TYPE_INT) {
            decodeIntImage(obj);
        } else if (buffer.getDataType() == DataBuffer.TYPE_BYTE) {
            decodeByteImage(obj);
        } else if (buffer.getDataType() == DataBuffer.TYPE_USHORT) {
            decodeShortImage(obj);
        } else {
            throw new DataFormatException(BAD_FORMAT);
        }
    }

    private void decodeIntImage(final BufferedImage obj)
                throws DataFormatException {

        final DataBuffer buffer = obj.getData().getDataBuffer();

        switch (obj.getType()) {
        case BufferedImage.TYPE_INT_ARGB:
            decodeARGB(buffer);
            break;
        case BufferedImage.TYPE_INT_ARGB_PRE:
            decodeARGBPre(buffer);
            break;
        case BufferedImage.TYPE_INT_BGR:
            decodeBGR(buffer);
            break;
        case BufferedImage.TYPE_INT_RGB:
            decodeRGB(buffer);
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }

    }

    private void decodeARGB(final DataBuffer buffer) {
        final int[] pixels = ((DataBufferInt) buffer).getData();
        format = ImageFormat.RGBA;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int pixel = pixels[y * width + x];

                image[index + ALPHA] = (byte) (pixel >> ALIGN_BYTE4);
                image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                image[index] = (byte) pixel;
            }
        }
    }

    private void decodeARGBPre(final DataBuffer buffer) {
        final int[] pixels = ((DataBufferInt) buffer).getData();
        format = ImageFormat.RGBA;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int pixel = pixels[y * width + x];

                image[index + ALPHA] = (byte) (pixel >> ALIGN_BYTE4);
                image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                image[index] = (byte) pixel;
            }
        }
    }

    private void decodeBGR(final DataBuffer buffer) {
        final int[] pixels = ((DataBufferInt) buffer).getData();
        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int pixel = pixels[y * width + x];

                image[index + ALPHA] = OPAQUE;
                image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                image[index] = (byte) pixel;
            }
        }
    }

    private void decodeRGB(final DataBuffer buffer) {
        final int[] pixels = ((DataBufferInt) buffer).getData();
        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int pixel = pixels[y * width + x];

                image[index + ALPHA] = OPAQUE;
                image[index] = (byte) (pixel >> ALIGN_BYTE3);
                image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                image[index + BLUE] = (byte) pixel;
            }
        }
    }

    private void decodeByteImage(final BufferedImage obj)
            throws IOException, DataFormatException {

        final DataBuffer buffer = obj.getData().getDataBuffer();

        switch (obj.getType()) {
        case BufferedImage.TYPE_3BYTE_BGR:
            decodeByteBGR(buffer);
            break;
        case BufferedImage.TYPE_CUSTOM:
            decodeByteCustom(buffer);
            break;
        case BufferedImage.TYPE_4BYTE_ABGR:
            decodeByteABGR(buffer);
            break;
        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            decodeByteABGRPre(buffer);
            break;
        case BufferedImage.TYPE_BYTE_BINARY:
            decodeByteBinary(buffer, obj.getColorModel());
            break;
        case BufferedImage.TYPE_BYTE_GRAY:
            decodeByteGray(buffer);
            break;
        case BufferedImage.TYPE_BYTE_INDEXED:
            decodeByteIndexed(buffer, obj.getColorModel());
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }
    }

    private void decodeByteBGR(final DataBuffer buffer) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int offset = 3 * (y * width + x);

                image[index + ALPHA] = OPAQUE;
                image[index + BLUE] = pixels[offset];
                image[index + GREEN] = pixels[offset + 1];
                image[index] = pixels[offset + 2];
            }
        }
    }

    private void decodeByteCustom(final DataBuffer buffer) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        if (width * height * RGB_CHANNELS == pixels.length) {
            format = ImageFormat.RGBA;
            image = new byte[height * width * BYTES_PER_PIXEL];
            int index = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final int offset = 3 * (y * width + x);

                    image[index] = pixels[offset];
                    image[index + GREEN] = pixels[offset + 1];
                    image[index + BLUE] = pixels[offset + 2];
                    image[index + ALPHA] = OPAQUE;
                    index += BYTES_PER_PIXEL;
                }
            }
        } else if (width * height * BYTES_PER_PIXEL == pixels.length) {
            format = ImageFormat.RGBA;
            image = new byte[height * width * BYTES_PER_PIXEL];
            int index = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final int offset = BYTES_PER_PIXEL
                            * (y * width + x);

                    image[index] = pixels[offset];
                    image[index + GREEN] = pixels[offset + 1];
                    image[index + BLUE] = pixels[offset + 2];
                    image[index + ALPHA] = pixels[offset + ALPHA];
                    index += BYTES_PER_PIXEL;
                }
            }
        }
    }

    private void decodeByteABGR(final DataBuffer buffer) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.RGBA;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int offset = BYTES_PER_PIXEL * (y * width + x);

                image[index + ALPHA] = pixels[offset];
                image[index + BLUE] = pixels[offset + 1];
                image[index + GREEN] = pixels[offset + 2];
                image[index] = pixels[offset + ALPHA];
            }
        }
    }

    private void decodeByteABGRPre(final DataBuffer buffer) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.RGBA;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                final int offset = BYTES_PER_PIXEL * (y * width + x);

                image[index + ALPHA] = pixels[offset];
                image[index + BLUE] = pixels[offset + 1];
                image[index + GREEN] = pixels[offset + 2];
                image[index] = pixels[offset + ALPHA];
            }
        }
    }

    private void decodeByteBinary(final DataBuffer buffer,
            final ColorModel model) throws IOException {

        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.IDX8;
        image = new byte[height * width];
        final int depth = model.getPixelSize();
        decodeColorTable(model);

        int index = 0;
        final ByteArrayInputStream stream = new ByteArrayInputStream(pixels);
        final LittleDecoder coder = new LittleDecoder(stream);

        for (int y = 0; y < height; y++) {
//          coder.mark();
            for (int x = 0; x < width; x++, index++) {
                image[index] = (byte) coder.readBits(depth, false);
            }
//          coder.alignToWord();
//          coder.unmark();
        }
    }

    private void decodeByteGray(final DataBuffer buffer) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int offset = (y * width + x);

                image[index + ALPHA] = OPAQUE;
                image[index + BLUE] = pixels[offset];
                image[index + GREEN] = pixels[offset];
                image[index] = pixels[offset];
                index += BYTES_PER_PIXEL;
            }
        }
    }

    private void decodeByteIndexed(final DataBuffer buffer,
            final ColorModel model) {
        final byte[] pixels = ((DataBufferByte) buffer).getData();

        format = ImageFormat.IDX8;
        image = new byte[height * width];
        decodeColorTable(model);

        int index = 0;

        for (int y = 0; y < height; y++) {
            System.arraycopy(pixels, y * width, image, index, width);
            index += width;
        }
    }

    private void decodeShortImage(final BufferedImage obj)
            throws DataFormatException {

        final DataBuffer buffer = obj.getData().getDataBuffer();

        switch (obj.getType()) {
//        case BufferedImage.TYPE_USHORT_555_RGB:
//            throw new DataFormatException(BAD_FORMAT);
//        case BufferedImage.TYPE_USHORT_565_RGB:
//            throw new DataFormatException(BAD_FORMAT);
        case BufferedImage.TYPE_USHORT_GRAY:
            decodeShortGray(buffer);
            break;
//        case BufferedImage.TYPE_CUSTOM:
//            decodeShortCustom(buffer);
//            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }
    }

    private void decodeShortGray(final DataBuffer buffer) {
        final short[] pixels = ((DataBufferUShort) buffer).getData(); //NOPMD
        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // int row = height-y-1;
                final int offset = (y * width + x);

                image[index + ALPHA] = OPAQUE;
                image[index + BLUE] = (byte) pixels[offset];
                image[index + GREEN] = (byte) pixels[offset];
                image[index] = (byte) pixels[offset];
                index += BYTES_PER_PIXEL;
            }
        }
    }

//    private void decodeShortCustom(final DataBuffer buffer) {
//        final short[] pixels = ((DataBufferUShort) buffer).getData();
//
//        if (width * height * RGB_CHANNELS == pixels.length) {
//            format = ImageFormat.RGBA;
//            image = new byte[height * width * BYTES_PER_PIXEL];
//            int index = 0;
//
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    final int offset = 3 * (y * width + x);
//
//                    image[index] = (byte) pixels[offset];
//                    image[index + GREEN] = (byte) pixels[offset + 1];
//                    image[index + BLUE] = (byte) pixels[offset + 2];
//                    image[index + ALPHA] = OPAQUE;
//                    index += BYTES_PER_PIXEL;
//                }
//            }
//        } else if (width * height * BYTES_PER_PIXEL == pixels.length) {
//            format = ImageFormat.RGBA;
//            image = new byte[height * width * BYTES_PER_PIXEL];
//            int index = 0;
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    final int offset = BYTES_PER_PIXEL
//                            * (y * width + x);
//
//                    image[index] = (byte) pixels[offset];
//                    image[index + GREEN] = (byte) pixels[offset + 1];
//                    image[index + BLUE] = (byte) pixels[offset + 2];
//                    image[index + ALPHA] = (byte) pixels[offset + ALPHA];
//                    index += BYTES_PER_PIXEL;
//                }
//            }
//        }
//    }

    /**
     * Decode the ColourModel used for indexed images.
     * @param model The ColorModel used by a BufferedImage.
     */
    private void decodeColorTable(final ColorModel model) {
        if (model instanceof IndexColorModel) {
            final IndexColorModel indexModel = (IndexColorModel) model;

            table = new byte[indexModel.getMapSize() * COLOUR_CHANNELS];

            final byte[] reds = new byte[indexModel.getMapSize()];
            final byte[] blues = new byte[indexModel.getMapSize()];
            final byte[] greens = new byte[indexModel.getMapSize()];

            indexModel.getReds(reds);
            indexModel.getGreens(greens);
            indexModel.getBlues(blues);

            int index = 0;

            for (int i = 0; i < table.length; i += COLOUR_CHANNELS) {
                table[i + RED] = reds[index];
                table[i + GREEN] = greens[index];
                table[i + BLUE] = blues[index];
                table[i + ALPHA] = -1;
                index++;
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

        for (int i = 0; i < img.length; i += BYTES_PER_PIXEL) {
            alpha = img[i + ALPHA];

            img[i + ALPHA] = img[i + BLUE];
            img[i + BLUE] = img[i + GREEN];
            img[i + GREEN] = img[i];
            img[i] = alpha;
        }
    }

    /**
     * Apply the level for the alpha channel to the red, green and blue colour
     * channels for encoding the image so it can be added to a Flash movie.
     * @param img the image data.
     */
    private void applyAlpha(final byte[] img) {
        int alpha;

        for (int i = 0; i < img.length; i += BYTES_PER_PIXEL) {
            alpha = img[i + ALPHA] & MASK_8BIT;

            img[i + ALPHA] = (byte) (((img[i + BLUE] & MASK_8BIT) * alpha)
                    / OPAQUE);
            img[i + BLUE] = (byte) (((img[i + GREEN] & MASK_8BIT) * alpha)
                    / OPAQUE);
            img[i + GREEN] = (byte) (((img[i + RED] & MASK_8BIT) * alpha)
                    / OPAQUE);
            img[i + RED] = (byte) alpha;
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
        final byte[] merged = new byte[(colors.length / BYTES_PER_PIXEL)
                                       * RGB_CHANNELS + img.length];
        int dst = 0;

        for (int i = 0; i < colors.length; i += BYTES_PER_PIXEL) {
            merged[dst++] = colors[i + RED];
            merged[dst++] = colors[i + GREEN];
            merged[dst++] = colors[i + BLUE];
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

        scan = (imgWidth + WORD_ALIGN) & ~WORD_ALIGN;
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
                final int red = (img[src++] & RGB5_MSB_MASK)
                        << RGB5_RED_SHIFT;
                final int green = (img[src++] & RGB5_MSB_MASK)
                        << RGB5_GREEN_SHIFT;
                final int blue = (img[src++] & RGB5_MSB_MASK)
                        >> RGB5_BLUE_SHIFT;
                final int colour = (red | green | blue) & Coder.LOWEST15;

                formattedImage[dst++] = (byte) (colour >> ALIGN_BYTE2);
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
