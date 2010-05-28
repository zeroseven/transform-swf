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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
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
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.LittleDecoder;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.ImageFormat;

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
    /** Mask applied to extract 5-bit values. */
    private static final int MASK_5BIT = 0x001F;
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
         read(new FileInputStream(file), (int) file.length());
    }

    /** {@inheritDoc} */
    public void read(final URL url) throws IOException, DataFormatException {
        final URLConnection connection = url.openConnection();
        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        read(url.openStream(), fileSize);
    }

    /** {@inheritDoc} */
    public void read(final InputStream stream, final int size)
            throws IOException, DataFormatException {
        read(ImageIO.read(stream));
    }

    /** {@inheritDoc} */
    public ImageTag defineImage(final int identifier) {
        ImageTag object = null;

        switch (format) {
        case IDX8:
            object = new DefineImage(identifier, width, height,
                    table.length / 4,
                    zip(merge(adjustScan(width, height, image), table)));
            break;
        case IDXA:
            object = new DefineImage2(identifier, width, height,
                    table.length / 4,
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
            throws DataFormatException {
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
            throw new DataFormatException(BAD_FORMAT);
        }
        return object;
    }

    /**
     * Create a BufferedImage from a Flash image.
     *
     * @param definition
     *            an image from a Flash file.
     *
     * @return a BufferedImage containing the image.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the BufferedImage.
     */
    public BufferedImage bufferedImage(final DefineImage definition)
            throws DataFormatException {
        BufferedImage bufferedImage = null;

        ImageFormat fmt;
        int imageWidth = 0;
        int imageHeight = 0;

        byte[] colourTable = null;
        byte[] indexedImage = null;
        byte[] colorImage = null;

        imageWidth = definition.getWidth();
        imageHeight = definition.getHeight();

        final byte[] data = unzip(definition.getImage(),
                imageWidth, imageHeight);

        final int scanLength = (imageWidth + WORD_ALIGN) & ~WORD_ALIGN;
        final int tableLength = definition.getTableSize();
        final int pixelLength = definition.getPixelSize();

        int pos = 0;
        int index = 0;

        if (tableLength > 0) {
            fmt = ImageFormat.IDX8;
            imageWidth = definition.getWidth();
            imageHeight = definition.getHeight();
            colourTable = new byte[tableLength * BYTES_PER_PIXEL];
            indexedImage = new byte[imageHeight * imageWidth];

            for (int i = 0; i < tableLength; i++, index += BYTES_PER_PIXEL) {
                colourTable[index + ALPHA] = OPAQUE;
                colourTable[index + BLUE] = data[pos++];
                colourTable[index + GREEN] = data[pos++];
                colourTable[index] = data[pos++];
            }

            index = 0;

            for (int h = 0; h < imageHeight; h++) {
                for (int w = 0; w < imageWidth; w++, index++) {
                    indexedImage[index] = data[pos++];
                }
                pos += (scanLength - imageWidth);
            }
        } else {
            fmt = ImageFormat.RGB8;
            imageWidth = definition.getWidth();
            imageHeight = definition.getHeight();
            colorImage = new byte[imageHeight * imageWidth * BYTES_PER_PIXEL];
            index = 0;

            if (pixelLength == 16) {
                for (int h = 0; h < imageHeight; h++) {
                    for (int w = 0; w < imageWidth; w++) {
                        final int color = (data[pos++] << ALIGN_BYTE2
                                | (data[pos++] & MASK_8BIT)) & 0x7FFF;

                        colorImage[index + ALPHA] = OPAQUE;
                        colorImage[index + RED] = (byte) (color >> 10);
                        colorImage[index + GREEN] = (byte) ((color >> 5)
                                & MASK_5BIT);
                        colorImage[index + BLUE] = (byte) (color & MASK_5BIT);
                        index += BYTES_PER_PIXEL;
                    }
                    pos += (scanLength - imageWidth);
                }
            } else {
                index = 0;

                for (int h = 0; h < imageHeight; h++) {
                    for (int w = 0; w < imageWidth; w++) {
                        colorImage[index + ALPHA] = OPAQUE;
                        colorImage[index + RED] = data[pos++];
                        colorImage[index + GREEN] = data[pos++];
                        colorImage[index + BLUE] = data[pos++];
                        index += BYTES_PER_PIXEL;
                    }
                    pos += (scanLength - imageWidth);
                }
            }
        }

        switch (fmt) {
        case IDX8:
        case IDXA:
            final byte[] red = new byte[colourTable.length];
            final byte[] green = new byte[colourTable.length];
            final byte[] blue = new byte[colourTable.length];
            final byte[] alpha = new byte[colourTable.length];
            index = 0;

            for (int i = 0; i < colourTable.length; i++) {
                red[i] = colourTable[index + BLUE];
                green[i] = colourTable[index + GREEN];
                blue[i] = colourTable[index + RED];
                alpha[i] = colourTable[index + ALPHA];
                index += BYTES_PER_PIXEL;
            }

            bufferedImage = new BufferedImage(imageWidth, imageHeight,
                    BufferedImage.TYPE_INT_ARGB);

            final int[] indexedBuffer = new int[imageWidth];
            int color;
            index = 0;

            for (int i = 0; i < imageHeight; i++) {
                for (int j = 0; j < imageWidth; j++, index++) {
                    color = indexedImage[index] << 2;

                    indexedBuffer[j] = (colourTable[color + ALPHA] & MASK_8BIT)
                                    << ALIGN_BYTE4;
                    indexedBuffer[j] = indexedBuffer[j]
                            | ((colourTable[color + 2] & MASK_8BIT)
                                    << ALIGN_BYTE3);
                    indexedBuffer[j] = indexedBuffer[j]
                            | ((colourTable[color + 1] & MASK_8BIT)
                                    << ALIGN_BYTE2);
                    indexedBuffer[j] = indexedBuffer[j]
                            | (colourTable[color + 0] & MASK_8BIT);
                }

                bufferedImage.setRGB(0, i, imageWidth, 1, indexedBuffer,
                        0, imageWidth);
            }
            break;
        case RGB5:
        case RGB8:
        case RGBA:
            bufferedImage = new BufferedImage(imageWidth, imageHeight,
                    BufferedImage.TYPE_INT_ARGB);

            final int[] directBuffer = new int[imageWidth];
            index = 0;

            for (int i = 0; i < imageHeight; i++) {
                for (int j = 0; j < imageWidth; j++, index += BYTES_PER_PIXEL) {
                    // int a = colorImage[i][j][3] & 0xFF;

                    /*
                     * directBuffer[j] = (colorImage[i][j][3] << 24) |
                     * (colorImage[i][j][0] << 16) | (colorImage[i][j][1] << 8)
                     * | colorImage[i][j][2];
                     */
                    directBuffer[j] = (colorImage[index + ALPHA] & MASK_8BIT)
                                    << ALIGN_BYTE4;
                    directBuffer[j] = directBuffer[j]
                            | ((colorImage[index + RED] & MASK_8BIT)
                                    << ALIGN_BYTE3);
                    directBuffer[j] = directBuffer[j]
                            | ((colorImage[index + GREEN] & MASK_8BIT)
                                    << ALIGN_BYTE2);
                    directBuffer[j] = directBuffer[j]
                            | (colorImage[index + BLUE] & MASK_8BIT);
                }
                bufferedImage.setRGB(0, i, imageWidth, 1, directBuffer,
                        0, imageWidth);
            }
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }

        return bufferedImage;
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
     * Create a BufferedImage from a Flash image.
     *
     * @param definition
     *            an image from a Flash file.
     *
     * @return a BufferedImage containing the image.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the BufferedImage.
     */
    public BufferedImage bufferedImage(final DefineImage2 definition)
            throws DataFormatException {
        BufferedImage bufferedImage = null;

        ImageFormat fmt;
        int imgWidth = 0;
        int imgHeight = 0;

        byte[] colourTable = null;
        byte[] indexedImage = null;
        byte[] colorImage = null;

        imgWidth = definition.getWidth();
        imgHeight = definition.getHeight();

        final byte[] data = unzip(definition.getImage(), imgWidth, imgHeight);

        final int scanLength = (imgWidth + WORD_ALIGN) & ~WORD_ALIGN;
        final int tableLength = definition.getTableSize();
        // int pixelLength = image.getPixelSize();

        int pos = 0;
        int index = 0;

        if (tableLength > 0) {
            fmt = ImageFormat.IDXA;
            imgWidth = definition.getWidth();
            imgHeight = definition.getHeight();
            colourTable = new byte[tableLength * BYTES_PER_PIXEL];
            indexedImage = new byte[imgHeight * imgWidth];

            for (int i = 0; i < tableLength; i++, index += BYTES_PER_PIXEL) {
                colourTable[index + ALPHA] = data[pos++];
                colourTable[index + BLUE] = data[pos++];
                colourTable[index + GREEN] = data[pos++];
                colourTable[index] = data[pos++];
            }

            index = 0;

            for (int h = 0; h < imgHeight; h++) {
                for (int w = 0; w < imgWidth; w++, index++) {
                    indexedImage[index] = data[pos++];
                }
                pos += (scanLength - imgWidth);
            }
        } else {
            fmt = ImageFormat.RGBA;
            imgWidth = definition.getWidth();
            imgHeight = definition.getHeight();
            colorImage = new byte[imgHeight * imgWidth * BYTES_PER_PIXEL];

            for (int h = 0; h < imgHeight; h++) {
                for (int w = 0; w < imgWidth; w++, index += BYTES_PER_PIXEL) {
                    colorImage[index + ALPHA] = data[pos++];
                    colorImage[index + RED] = data[pos++];
                    colorImage[index + GREEN] = data[pos++];
                    colorImage[index + BLUE] = data[pos++];
                }
            }
        }

        switch (fmt) {
        case IDX8:
        case IDXA:
            final byte[] red = new byte[colourTable.length];
            final byte[] green = new byte[colourTable.length];
            final byte[] blue = new byte[colourTable.length];
            final byte[] alpha = new byte[colourTable.length];
            index = 0;

            for (int i = 0; i < colourTable.length; i++) {
                red[i] = colourTable[index + BLUE];
                green[i] = colourTable[index + GREEN];
                blue[i] = colourTable[index + RED];
                alpha[i] = colourTable[index + ALPHA];
                index += BYTES_PER_PIXEL;
            }

            bufferedImage = new BufferedImage(imgWidth, imgHeight,
                    BufferedImage.TYPE_INT_ARGB);

            final int[] indexedBuffer = new int[imgWidth];
            int color;
            index = 0;

            for (int i = 0; i < imgHeight; i++) {
                for (int j = 0; j < imgWidth; j++, index++) {
                    color = indexedImage[index] << 2;

                    indexedBuffer[j] = (colourTable[color + ALPHA] & MASK_8BIT)
                                    << ALIGN_BYTE4;
                    indexedBuffer[j] = indexedBuffer[j]
                            | ((colourTable[color + 2] & MASK_8BIT)
                                    << ALIGN_BYTE3);
                    indexedBuffer[j] = indexedBuffer[j]
                            | ((colourTable[color + 1] & MASK_8BIT)
                                    << ALIGN_BYTE2);
                    indexedBuffer[j] = indexedBuffer[j]
                            | (colourTable[color + 0] & MASK_8BIT);
                }

                bufferedImage.setRGB(0, i, imgWidth, 1,
                        indexedBuffer, 0, imgWidth);
            }
            break;
        case RGB5:
        case RGB8:
        case RGBA:
            bufferedImage = new BufferedImage(imgWidth, imgHeight,
                    BufferedImage.TYPE_INT_ARGB);

            final int[] directBuffer = new int[imgWidth];
            index = 0;

            for (int i = 0; i < imgHeight; i++) {
                for (int j = 0; j < imgWidth; j++, index += BYTES_PER_PIXEL) {
                    // int a = colorImage[i][j][3] & 0xFF;

                    /*
                     * directBuffer[j] = (colorImage[i][j][3] << 24) |
                     * (colorImage[i][j][0] << 16) | (colorImage[i][j][1] << 8)
                     * | colorImage[i][j][2];
                     */
                    directBuffer[j] = (colorImage[index + ALPHA] & MASK_8BIT)
                                    << ALIGN_BYTE4;
                    directBuffer[j] = directBuffer[j]
                            | ((colorImage[index + RED] & MASK_8BIT)
                                    << ALIGN_BYTE3);
                    directBuffer[j] = directBuffer[j]
                            | ((colorImage[index + GREEN] & MASK_8BIT)
                                    << ALIGN_BYTE2);
                    directBuffer[j] = directBuffer[j]
                            | (colorImage[index + BLUE] & MASK_8BIT);
                }
                bufferedImage.setRGB(0, i, imgWidth, 1,
                        directBuffer, 0, imgWidth);
            }
            break;
        default:
            throw new DataFormatException(BAD_FORMAT);
        }

        return bufferedImage;
    }

    /**
     * Resizes a BufferedImage to the specified width and height. The aspect
     * ratio of the image is maintained so the area in the new image not covered
     * by the resized original will be transparent.
     *
     * @param bufferedImg
     *            the BufferedImage to resize.
     * @param imgWidth
     *            the width of the resized image in pixels.
     * @param imgHeight
     *            the height of the resized image in pixels.
     * @return a new BufferedImage with the specified width and height.
     */
    public BufferedImage resizeImage(final BufferedImage bufferedImg,
            final int imgWidth, final int imgHeight) {
        int imageType = bufferedImg.getType();

        if (imageType == BufferedImage.TYPE_CUSTOM) {
            imageType = BufferedImage.TYPE_4BYTE_ABGR;
        }

        final BufferedImage resized = new BufferedImage(imgWidth, imgHeight,
                BufferedImage.TYPE_4BYTE_ABGR);

        final double widthRatio = (double) bufferedImg.getWidth()
                / (double) imgWidth;
        final double heightRatio = (double) bufferedImg.getHeight()
                / (double) imgHeight;
        double ratio = (widthRatio > heightRatio ? widthRatio : heightRatio);

        if (ratio < 1.0) {
            ratio = 1.0;
        }

        final int imageWidth = (int) (bufferedImg.getWidth() / ratio);
        final int imageHeight = (int) (bufferedImg.getHeight() / ratio);

        final int xCoord = (imgWidth - imageWidth) / 2;
        final int yCoord = (imgHeight - imageHeight) / 2;

        final Graphics2D graphics = resized.createGraphics();
        graphics.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        graphics.fillRect(0, 0, imgWidth, imgHeight);

        final java.awt.Image scaled = bufferedImg.getScaledInstance(imageWidth,
                imageHeight, java.awt.Image.SCALE_SMOOTH);
        new javax.swing.ImageIcon(scaled);

        graphics.drawImage(scaled, xCoord, yCoord, null);
        graphics.dispose();
        resized.flush();

        new javax.swing.ImageIcon(resized).getImage();

        return resized;
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
    public void read(final BufferedImage obj) throws DataFormatException {

        final DataBuffer buffer = obj.getData().getDataBuffer();

        width = obj.getWidth();
        height = obj.getHeight();

        int index;

        if (buffer.getDataType() == DataBuffer.TYPE_INT) {
            final int[] pixels = ((DataBufferInt) buffer).getData();

            switch (obj.getType()) {
            case BufferedImage.TYPE_INT_ARGB:
                format = ImageFormat.RGBA;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int pixel = pixels[y * width + x];

                        image[index + ALPHA] = (byte) (pixel >> ALIGN_BYTE4);
                        image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                        image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                        image[index] = (byte) pixel;
                    }
                }
                break;
            case BufferedImage.TYPE_INT_ARGB_PRE:
                format = ImageFormat.RGBA;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int pixel = pixels[y * width + x];

                        image[index + ALPHA] = (byte) (pixel >> ALIGN_BYTE4);
                        image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                        image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                        image[index] = (byte) pixel;
                    }
                }
                break;
            case BufferedImage.TYPE_INT_BGR:
                format = ImageFormat.RGB8;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int pixel = pixels[y * width + x];

                        image[index + ALPHA] = OPAQUE;
                        image[index + BLUE] = (byte) (pixel >> ALIGN_BYTE3);
                        image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                        image[index] = (byte) pixel;
                    }
                }
                break;
            case BufferedImage.TYPE_INT_RGB:
                format = ImageFormat.RGB8;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int pixel = pixels[y * width + x];

                        image[index + ALPHA] = OPAQUE;
                        image[index] = (byte) (pixel >> ALIGN_BYTE3);
                        image[index + GREEN] = (byte) (pixel >> ALIGN_BYTE2);
                        image[index + BLUE] = (byte) pixel;
                    }
                }
                break;
            default:
                throw new DataFormatException(BAD_FORMAT);
            }

        } else if (buffer.getDataType() == DataBuffer.TYPE_BYTE) {
            final byte[] pixels = ((DataBufferByte) buffer).getData();

            switch (obj.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                format = ImageFormat.RGB8;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int offset = 3 * (y * width + x);

                        image[index + ALPHA] = OPAQUE;
                        image[index + BLUE] = pixels[offset];
                        image[index + GREEN] = pixels[offset + 1];
                        image[index] = pixels[offset + 2];
                    }
                }
                break;
            case BufferedImage.TYPE_CUSTOM:
                if (width * height * 3 == pixels.length) {
                    format = ImageFormat.RGBA;
                    image = new byte[height * width * BYTES_PER_PIXEL];
                    index = 0;

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
                }
                if (width * height * BYTES_PER_PIXEL == pixels.length) {
                    format = ImageFormat.RGBA;
                    image = new byte[height * width * BYTES_PER_PIXEL];
                    index = 0;
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
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                format = ImageFormat.RGBA;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int offset = BYTES_PER_PIXEL * (y * width + x);

                        image[index + ALPHA] = pixels[offset];
                        image[index + BLUE] = pixels[offset + 1];
                        image[index + GREEN] = pixels[offset + 2];
                        image[index] = pixels[offset + ALPHA];
                    }
                }
                break;
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                format = ImageFormat.RGBA;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++, index += BYTES_PER_PIXEL) {
                        final int offset = BYTES_PER_PIXEL * (y * width + x);

                        image[index + ALPHA] = pixels[offset];
                        image[index + BLUE] = pixels[offset + 1];
                        image[index + GREEN] = pixels[offset + 2];
                        image[index] = pixels[offset + ALPHA];
                    }
                }
                break;
            case BufferedImage.TYPE_BYTE_BINARY:
                format = ImageFormat.IDX8;
                image = new byte[height * width];
                int depth = obj.getColorModel().getPixelSize();
                decodeColorTable(obj.getColorModel());

                index = 0;
                final LittleDecoder coder = new LittleDecoder(pixels);

                for (int y = 0; y < height; y++) {
                    int bitsRead = 0;

                    for (int x = 0; x < width; x++, index++) {
                        image[index] = (byte) coder.readBits(depth, false);
                        bitsRead += depth;
                    }
                    if (bitsRead % 32 > 0) {
                        coder.adjustPointer(32 - (bitsRead % 32));
                    }
                }
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                format = ImageFormat.RGB8;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

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
                break;
            case BufferedImage.TYPE_BYTE_INDEXED:
                format = ImageFormat.IDX8;
                image = new byte[height * width];
                depth = obj.getColorModel().getPixelSize();
                decodeColorTable(obj.getColorModel());

                index = 0;

                for (int y = 0; y < height; y++) {
                    System.arraycopy(pixels, y * width, image, index, width);
                    index += width;
                }
                break;
            default:
                throw new DataFormatException(BAD_FORMAT);
            }
        } else if (buffer.getDataType() == DataBuffer.TYPE_USHORT) {
            final short[] pixels = ((DataBufferUShort) buffer).getData();
            // AvoidUsingShortType

            switch (obj.getType()) {
            case BufferedImage.TYPE_USHORT_555_RGB:
                throw new DataFormatException(BAD_FORMAT);
            case BufferedImage.TYPE_USHORT_565_RGB:
                throw new DataFormatException(BAD_FORMAT);
            case BufferedImage.TYPE_USHORT_GRAY:
                format = ImageFormat.RGB8;
                image = new byte[height * width * BYTES_PER_PIXEL];
                index = 0;

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
                break;
            default:
                throw new DataFormatException(BAD_FORMAT);
            }
        } else {
            throw new DataFormatException(BAD_FORMAT);
        }
    }

    /**
     * Decode the ColourModel used for indexed images.
     * @param model The ColorModel used by a BufferedImage.
     */
    private void decodeColorTable(final ColorModel model) {
        if (model instanceof IndexColorModel) {
            IndexColorModel indexModel = (IndexColorModel) model;

            table = new byte[indexModel.getMapSize() * 4];

            byte[] reds = new byte[indexModel.getMapSize()];
            byte[] blues = new byte[indexModel.getMapSize()];
            byte[] greens = new byte[indexModel.getMapSize()];

            indexModel.getReds(reds);
            indexModel.getGreens(greens);
            indexModel.getBlues(blues);

            int index = 0;

            for (int i = 0; i < table.length; i += 4) {
                table[i] = reds[index];
                table[i + 1] = greens[index];
                table[i + 2] = blues[index];
                table[i + 3] = -1;
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

            img[i + 3] = (byte) (((img[i + 2] & MASK_8BIT) * alpha)
                    / OPAQUE);
            img[i + 2] = (byte) (((img[i + 1] & MASK_8BIT) * alpha)
                    / OPAQUE);
            img[i + 1] = (byte) (((img[i] & MASK_8BIT) * alpha) / OPAQUE);
            img[i] = (byte) alpha;
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
                                       * 3 + img.length];
        int dst = 0;

        for (int i = 0; i < colors.length; i += BYTES_PER_PIXEL) {
            merged[dst++] = colors[i]; // R
            merged[dst++] = colors[i + 1]; // G
            merged[dst++] = colors[i + 2]; // B
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
                final int red = (img[src++] & 0xF8) << 7;
                final int green = (img[src++] & 0xF8) << 2;
                final int blue = (img[src++] & 0xF8) >> 3;
                final int colour = (red | green | blue) & 0x7FFF;

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

    /**
     * Uncompress the image using the ZIP format.
     * @param bytes the compressed image data.
     * @param imgWidth the width of the image in pixels.
     * @param imgHeight the height of the image in pixels.
     * @return the uncompressed image.
     * @throws DataFormatException if the compressed image is not in the ZIP
     * format or cannot be uncompressed.
     */
    private byte[] unzip(final byte[] bytes, final int imgWidth,
            final int imgHeight) throws DataFormatException {
        final byte[] data = new byte[imgWidth * imgHeight * 8];
        int count = 0;

        final Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        count = inflater.inflate(data);
        inflater.end();

        final byte[] uncompressedData = new byte[count];

        System.arraycopy(data, 0, uncompressedData, 0, count);

        return uncompressedData;
    }
}
