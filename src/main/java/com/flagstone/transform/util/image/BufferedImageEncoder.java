/*
 * BufferedImageEncoder.java
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

package com.flagstone.transform.util.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.DefineImage2;
import com.flagstone.transform.image.ImageFormat;
import com.flagstone.transform.image.ImageTag;

/**
 * BufferedImageEncoder generates BufferedImages from Flash image definitions.
 */
public final class BufferedImageEncoder {
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

    /** Size of a pixel in a RGB555 true colour image. */
    private static final int RGB5_SIZE = 16;

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

    /**
     * Decode an ImageTeg definition.
     *
     * @param definition
     *            a DefineImage object.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image definition.
     */
    public void setImage(final ImageTag definition)
            throws DataFormatException {

        if (definition instanceof DefineImage) {
            setImage((DefineImage) definition);
        } else if (definition instanceof DefineImage2) {
            setImage((DefineImage2) definition);
        }
    }

    /**
     * Decode a DefineImage definition.
     *
     * @param definition
     *            a DefineImage object.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image definition.
     */
    public void setImage(final DefineImage definition)
            throws DataFormatException {

        width = definition.getWidth();
        height = definition.getHeight();

        if (definition.getTableSize() > 0) {
            setIDX(definition);
        } else {
            if (definition.getPixelSize() == RGB5_SIZE) {
                setRGB5(definition);
            } else {
                setRGB8(definition);
            }
        }
     }

    /**
     * Decode a DefineImage2 definition.
     *
     * @param definition
     *            a DefineImage2 object.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image definition.
     */
    public void setImage(final DefineImage2 definition)
            throws DataFormatException {
        if (definition.getTableSize() > 0) {
            setIDXA(definition);
        } else {
            setRGBA(definition);
        }
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

    private void setIDX(final DefineImage definition)
            throws DataFormatException {

        final byte[] data = unzip(definition.getImage(), width, height);
        final int scanLength = (width + WORD_ALIGN) & ~WORD_ALIGN;
        final int tableLength = definition.getTableSize();

        int pos = 0;
        int index = 0;

        format = ImageFormat.IDX8;
        table = new byte[tableLength * BYTES_PER_PIXEL];
        image = new byte[height * width];

        for (int i = 0; i < tableLength; i++, index += BYTES_PER_PIXEL) {
            table[index + ALPHA] = OPAQUE;
            table[index + BLUE] = data[pos++];
            table[index + GREEN] = data[pos++];
            table[index] = data[pos++];
        }

        index = 0;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++, index++) {
                image[index] = data[pos++];
            }
            pos += (scanLength - width);
        }
    }

    private void setRGB5(final DefineImage definition)
            throws DataFormatException {
        final byte[] data = unzip(definition.getImage(), width, height);
        final int scanLength = (width + WORD_ALIGN) & ~WORD_ALIGN;

        int pos = 0;
        int index = 0;

        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                final int color = (data[pos++] << ALIGN_BYTE2
                        | (data[pos++] & MASK_8BIT)) & Coder.LOWEST15;

                image[index + ALPHA] = OPAQUE;
                image[index + RED] = (byte) (color >> 10);
                image[index + GREEN] = (byte) ((color >> 5)
                        & MASK_5BIT);
                image[index + BLUE] = (byte) (color & MASK_5BIT);
                index += BYTES_PER_PIXEL;
            }
            pos += (scanLength - width);
        }
    }

    private void setRGB8(final DefineImage definition)
                throws DataFormatException {

        final byte[] data = unzip(definition.getImage(), width, height);
        final int scanLength = (width + WORD_ALIGN) & ~WORD_ALIGN;

        int pos = 0;
        int index = 0;

        format = ImageFormat.RGB8;
        image = new byte[height * width * BYTES_PER_PIXEL];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                image[index + ALPHA] = OPAQUE;
                image[index + RED] = data[pos++];
                image[index + GREEN] = data[pos++];
                image[index + BLUE] = data[pos++];
                index += BYTES_PER_PIXEL;
            }
            pos += (scanLength - width);
        }
    }

    private void setIDXA(final DefineImage2 definition)
            throws DataFormatException {

        width = definition.getWidth();
        height = definition.getHeight();

        final byte[] data = unzip(definition.getImage(), width, height);
        final int scanLength = (width + WORD_ALIGN) & ~WORD_ALIGN;
        final int tableLength = definition.getTableSize();

        int pos = 0;
        int index = 0;

        format = ImageFormat.IDXA;
        table = new byte[tableLength * BYTES_PER_PIXEL];
        image = new byte[height * width];

        for (int i = 0; i < tableLength; i++, index += BYTES_PER_PIXEL) {
            table[index + ALPHA] = data[pos++];
            table[index + BLUE] = data[pos++];
            table[index + GREEN] = data[pos++];
            table[index] = data[pos++];
        }

        index = 0;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++, index++) {
                image[index] = data[pos++];
            }
            pos += (scanLength - width);
        }
    }

    private void setRGBA(final DefineImage2 definition)
            throws DataFormatException {

        width = definition.getWidth();
        height = definition.getHeight();

        final byte[] data = unzip(definition.getImage(), width, height);
        // final int scanLength = (imgWidth + WORD_ALIGN) & ~WORD_ALIGN;

        int pos = 0;
        int index = 0;

        image = new byte[height * width * BYTES_PER_PIXEL];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++, index += BYTES_PER_PIXEL) {
                image[index + ALPHA] = data[pos++];
                image[index + RED] = data[pos++];
                image[index + GREEN] = data[pos++];
                image[index + BLUE] = data[pos++];
            }
        }
    }

    /**
     * Create a BufferedImage from the decoded Flash image.
     *
     * @return a BufferedImage containing the image.
     */
    public BufferedImage getBufferedImage() {

        final BufferedImage bufferedImage;

        if (format == ImageFormat.IDX8 || format == ImageFormat.IDXA) {
            bufferedImage = getIndexedImage();
        } else {
            bufferedImage = getRGBAImage();
        }
        return bufferedImage;
    }

    private BufferedImage getIndexedImage() {

        final byte[] red = new byte[table.length];
        final byte[] green = new byte[table.length];
        final byte[] blue = new byte[table.length];
        final byte[] alpha = new byte[table.length];

        int count = table.length / BYTES_PER_PIXEL;
        int index = 0;

        for (int i = 0; i < count; i++) {
            red[i] = table[index + BLUE];
            green[i] = table[index + GREEN];
            blue[i] = table[index + RED];
            alpha[i] = table[index + ALPHA];
            index += BYTES_PER_PIXEL;
        }

        final BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);

        final int[] row = new int[width];
        int color;
        index = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++, index++) {
                color = (image[index] & MASK_8BIT) << 2;

                row[j] = (table[color + ALPHA] & MASK_8BIT)
                                << ALIGN_BYTE4;
                row[j] = row[j]
                        | ((table[color + 2] & MASK_8BIT)
                                << ALIGN_BYTE3);
                row[j] = row[j]
                        | ((table[color + 1] & MASK_8BIT)
                                << ALIGN_BYTE2);
                row[j] = row[j]
                        | (table[color + 0] & MASK_8BIT);
            }
            bufferedImage.setRGB(0, i, width, 1, row, 0, width);
        }
        return bufferedImage;
    }

    private BufferedImage getRGBAImage() {

        final BufferedImage bufferedImage = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_ARGB);

        final int[] buffer = new int[width];
        int index = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++, index += BYTES_PER_PIXEL) {
                buffer[j] = (image[index + ALPHA] & MASK_8BIT)
                                << ALIGN_BYTE4;
                buffer[j] = buffer[j]
                        | ((image[index + RED] & MASK_8BIT)
                                << ALIGN_BYTE3);
                buffer[j] = buffer[j]
                        | ((image[index + GREEN] & MASK_8BIT)
                                << ALIGN_BYTE2);
                buffer[j] = buffer[j]
                        | (image[index + BLUE] & MASK_8BIT);
            }
            bufferedImage.setRGB(0, i, width, 1, buffer, 0, width);
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

        final int xCoord = (imgWidth - imageWidth) >> 1;
        final int yCoord = (imgHeight - imageHeight) >> 1;

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
