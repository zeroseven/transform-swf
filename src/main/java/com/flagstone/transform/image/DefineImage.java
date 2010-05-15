/*
 * DefineImage.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.image;

import java.util.Arrays;

import com.flagstone.transform.SWF;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * DefineImage is used to define an image compressed using the lossless zlib
 * compression algorithm.
 *
 * <p>
 * The class supports colour-mapped images where the image data contains an
 * index into a colour table or direct-mapped images where the colour is
 * specified directly.
 * </p>
 *
 * <p>
 * For colour-mapped images the colour table contains up to 256, 24-bit colours.
 * The image contains one byte for each pixel which is an index into the table
 * to specify the colour for that pixel. The colour table and the image data are
 * compressed as a single block, with the colour table placed before the image.
 * </p>
 *
 * <p>
 * For images where the colour is specified directly, the image data contains
 * either 16 or 24 bit colour values. For 16-bit colour values the most
 * significant bit is zero followed by three, 5-bit fields for the red, green
 * and blue channels:
 * </p>
 *
 * <pre>
 *  +-+--------+--------+--------+
 *  |0|   Red  |  Green |  Blue  |
 *  +-+--------+--------+--------+
 *  15                            0
 * </pre>
 *
 * <p>
 * Four bytes are used to represent 24-bit colours. The first byte is always set
 * to zero and the following bytes contain the colour values for the red, green
 * and blue colour channels.
 * </p>
 *
 * <p>
 * The number of bytes in each row of an image must be aligned to a 32-bit word
 * boundary. For example if an image if an icon is 25 pixels wide, then for an
 * 8-bit colour mapped image an additional three bytes (0x00) must be used to
 * pad each row; for a 16-bit direct mapped colour image an additional two bytes
 * must be used as padding.
 * </p>
 *
 * <p>
 * The image data is stored in zlib compressed form within the object. For
 * colour-mapped images the compressed data contains the colour table followed
 * by the image data. The colour table is omitted for direct-mapped images.
 * </p>
 *
 * @see DefineImage2
 */
public final class DefineImage implements ImageTag {

    private static final String FORMAT = "DefineImage: { identifier=%d;"
            + " pixelSize=%d; width=%d; height=%d; tableSize=%d; image=%d }";

    private int identifier;
    private int width;
    private int height;
    private int pixelSize;
    private int tableSize;
    private byte[] image;

    private transient int length;
    private transient boolean extendLength;

    /**
     * Creates and initialises a DefineImage object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineImage(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();

        switch (coder.readByte()) {
        case 3:
            pixelSize = 8;
            break;
        case 4:
            pixelSize = 16;
            break;
        case 5:
            pixelSize = 24;
            break;
        default:
            pixelSize = 0;
            break;
        }

        width = coder.readUI16();
        height = coder.readUI16();

        if (pixelSize == 8) {
            tableSize = coder.readByte() + 1;
            image = coder.readBytes(new byte[length - 8]);
        } else {
            image = coder.readBytes(new byte[length - 7]);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a DefineImage object defining a colour-mapped image.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param imgWidth
     *            the width of the image. Must be in the range 0..65535.
     * @param imgHeight
     *            the height of the image. Must be in the range 0..65535.
     * @param size
     *            the number of entries in the colour table in the compressed
     *            data. Each entry is 24 bits. Must be in the range 1..256.
     * @param data
     *            the zlib compressed colour table and image data.
     */
    public DefineImage(final int uid, final int imgWidth, final int imgHeight,
            final int size, final byte[] data) {
        extendLength = true;
        setIdentifier(uid);
        setWidth(imgWidth);
        setHeight(imgHeight);
        setPixelSize(8);
        setTableSize(size);
        setImage(data);
    }

    /**
     * Creates a DefineImage object defining an true-colour image.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param imgWidth
     *            the width of the image. Must be in the range 0..65535.
     * @param imgHeight
     *            the height of the image. Must be in the range 0..65535.
     * @param data
     *            the zlib compressed image data.
     * @param size
     *            the size of each pixel, either 16 or 24 bits.
     */
    public DefineImage(final int uid, final int imgWidth, final int imgHeight,
            final byte[] data, final int size) {
        extendLength = true;
        setIdentifier(uid);
        setWidth(imgWidth);
        setHeight(imgHeight);
        setPixelSize(size);
        tableSize = 0;
        setImage(data);
    }

    /**
     * Creates and initialises a DefineImage object using the values copied
     * from another DefineImage object.
     *
     * @param object
     *            a DefineImage object from which the values will be
     *            copied.
     */
    public DefineImage(final DefineImage object) {
        extendLength = object.extendLength;
        identifier = object.identifier;
        width = object.width;
        height = object.height;
        pixelSize = object.pixelSize;
        tableSize = object.tableSize;
        image = object.image;
    }

    /** {@inheritDoc} */
    public int getIdentifier() {
        return identifier;
    }

    /** {@inheritDoc} */
    public void setIdentifier(final int uid) {
        if ((uid < SWF.MIN_IDENTIFIER) || (uid > SWF.MAX_IDENTIFIER)) {
            throw new IllegalArgumentRangeException(
                    SWF.MIN_IDENTIFIER, SWF.MAX_IDENTIFIER, uid);
        }
        identifier = uid;
    }

    /**
     * Returns the width of the image in pixels (not twips).
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image in pixels (not twips).
     *
     * @return the height of the image in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the number of bits used to represent each pixel. Either 8, 16 or
     * 24 bits. The pixel size is 8-bits for colour-mapped images and 16 or 24
     * bits for images where the colour is specified directly.
     *
     * @return the number of bits per pixel: 8, 16 or 24.
     */
    public int getPixelSize() {
        return pixelSize;
    }

    /**
     * Returns the number of entries in the colour table encoded the compressed
     * image. For images where the colour is specified directly in the image
     * then the table size is zero.
     */
    public int getTableSize() {
        return tableSize;
    }

    /**
     * Returns a copy of the compressed colour table and image.
     */
    public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Sets the width of the image in pixels.
     *
     * @param aNumber
     *            the width of the image. Must be in the range 0..65535.
     */
    public void setWidth(final int aNumber) {
        if ((aNumber < 0) || (aNumber > SWF.MAX_WIDTH)) {
            throw new IllegalArgumentRangeException(0, SWF.MAX_WIDTH, aNumber);
        }
        width = aNumber;
    }

    /**
     * Sets the height of the image in pixels.
     *
     * @param aNumber
     *            the height of the image in pixels. Must be in the range
     *            0..65535.
     */
    public void setHeight(final int aNumber) {
        if ((aNumber < 0) || (aNumber > SWF.MAX_HEIGHT)) {
            throw new IllegalArgumentRangeException(0, SWF.MAX_HEIGHT, aNumber);
        }
        height = aNumber;
    }

    /**
     * Sets the size of the pixel in bits: 8, 16 or 32. The pixel size is 8-bits
     * for colour-mapped images and 16 or 24 bits for images where the colour is
     * specified directly.
     *
     * @param size
     *            the size of each pixel in bits. Must be either 8, 16 or 24.
     */
    public void setPixelSize(final int size) {
        if ((size != 8) && (size != 16) && (size != 24)) {
            throw new IllegalArgumentException(
                    "Pixel size must be either 8, 16 or 24.");
        }
        pixelSize = size;
    }

    /**
     * Sets the number of entries in the colour table in the compressed image.
     * For images where the colour is specified directly in the image then the
     * table size should be zero.
     *
     * @param size
     *            the number of entries in the colour table in the compressed
     *            image, in the range 1..256.
     */
    public void setTableSize(final int size) {
        if ((size < 1) || (size > 256)) {
            throw new IllegalArgumentException(
                    "Colour table size must be in the range 1..256.");
        }
        tableSize = size;
    }

    /**
     * Sets the data containing the compressed image and colour table.
     *
     * @param bytes
     *            byte array containing zlib compressed colour table and image.
     *            Must not be null.
     */
    public void setImage(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        image = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public DefineImage copy() {
        return new DefineImage(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, pixelSize, width, height,
                tableSize, image.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 7;
        length += (pixelSize == 8) ? 1 : 0;
        length += image.length;

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        coder.writeWord((MovieTypes.DEFINE_IMAGE << 6) | 0x3F, 2);
        coder.writeI32(length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);

        switch (pixelSize) {
        case 8:
            coder.writeWord(3, 1);
            break;
        case 16:
            coder.writeWord(4, 1);
            break;
        case 24:
            coder.writeWord(5, 1);
            break;
        default:
            break;
        }

        coder.writeI16(width);
        coder.writeI16(height);

        if (pixelSize == 8) {
            coder.writeWord(tableSize - 1, 1);
        }

        coder.writeBytes(image);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
