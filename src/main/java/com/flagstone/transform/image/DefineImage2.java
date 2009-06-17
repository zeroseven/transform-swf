/*
 * DefineImage2.java
 * Transform
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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


import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * DefineImage2 is used to define a transparent image compressed using the
 * lossless zlib compression algorithm.
 *
 * <p>
 * The class supports colour-mapped images where the image data contains an
 * index into a colour table or direct-mapped images where the colour is
 * specified directly. It extends DefineImage by including alpha channel 
 * information for the colour table and pixels in the image.
 * </p>
 *
 * <p>
 * For colour-mapped images the colour table contains up to 256, 32-bit colours.
 * The image contains one byte for each pixel which is an index into the table
 * to specify the colour for that pixel. The colour table and the image data are
 * compressed as a single block, with the colour table placed before the image.
 * </p>
 *
 * <p>
 * For images where the colour is specified directly, the image data contains 32
 * bit colour values.
 * </p>
 *
 * <p>
 * The image data is stored in zlib compressed form within the object. For
 * colour-mapped images the compressed data contains the colour table followed
 * by the image data.
 * </p>
 *
 * @see DefineImage
 */
public final class DefineImage2 implements ImageTag {
    
    private static final String FORMAT = "DefineImage2: { identifier=%d;"
            + " width=%d; height=%d; pixelSize=%d; tableSize=%d; image=%d }";

    private int identifier;
    private int width;
    private int height;
    private int pixelSize;
    private int tableSize;
    private byte[] image;

    private transient int length;
    private transient boolean extendLength;

    /**
     * Creates and initialises an DefineImage2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineImage2(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);
        identifier = coder.readWord(2, false);

        if (coder.readByte() == 3) {
            pixelSize = 8;
        } else {
            pixelSize = 32;
        }

        width = coder.readWord(2, false);
        height = coder.readWord(2, false);

        if (pixelSize == 8) {
            tableSize = coder.readByte() + 1;
            image = coder.readBytes(new byte[length - 8]);
        } else {
            image = coder.readBytes(new byte[length - 7]);
        }

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineImage2 object defining a colour-mapped image.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param width
     *            the width of the image. Must be in the range 0..65535.
     * @param height
     *            the height of the image. Must be in the range 0..65535.
     * @param tableSize
     *            the number of entries in the colour table in the compressed
     *            data. Each entry is 32 bits. Must be in the range 1..256.
     * @param data
     *            the zlib compressed colour table and image data. Must not be
     *            null.
     */
    public DefineImage2(final int uid, final int width, final int height,
            final int tableSize, final byte[] data) {
        extendLength = true;
        setIdentifier(uid);
        setWidth(width);
        setHeight(height);
        setPixelSize(8);
        setTableSize(tableSize);
        setImage(data);
    }

    /**
     * Creates a DefineImage object defining a true-colour image. Each pixel in
     * the image is 32 bits - 8 bits for the red, green, blue and alpha colour
     * channels.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param width
     *            the width of the image. Must be in the range 0..65535.
     * @param height
     *            the height of the image. Must be in the range 0..65535.
     * @param data
     *            the zlib compressed image data. Must not be null.
     */
    public DefineImage2(final int uid, final int width, final int height,
            final byte[] data) {
        extendLength = true;
        setIdentifier(uid);
        setWidth(width);
        setHeight(height);
        setPixelSize(32);
        tableSize = 0;
        setImage(data);
    }

    /**
     * Creates and initialises a DefineImage2 object using the values copied
     * from another DefineImage2 object.
     *
     * @param object
     *            a DefineImage2 object from which the values will be
     *            copied.
     */
    public DefineImage2(final DefineImage2 object) {
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
        if ((uid < 1) || (uid > 65535)) {
             throw new IllegalArgumentRangeException(1, 65536, uid);
        }
        identifier = uid;
    }

    /**
     * Returns the width of the image in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the number of bits used to represent each pixel. Either 8 or 32
     * bits. The pixel size is 8-bits for colour-mapped images and 32 bits for
     * images where the colour is specified directly.
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
     * Returns a copy the data containing the compressed colour table and image.
     */
    public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Sets the width of the image in pixels.
     *
     * @param aNumber
     *            the width of the image. Must be in the range of 0..65535.
     */
    public void setWidth(final int aNumber) {
        width = aNumber;
    }

    /**
     * Sets the height of the image in pixels.
     *
     * @param aNumber
     *            the height of the image. Must be in the range of 0..65535.
     */
    public void setHeight(final int aNumber) {
        height = aNumber;
    }

    /**
     * Sets the size of the pixel in bits: 8 for colour-mapped images, 32 for
     * direct images.
     *
     * @param size
     *            the size of each pixel in bits: must be either 8 or 32.
     */
    public void setPixelSize(final int size) {
        if ((size != 8) && (size != 32)) {
            throw new IllegalArgumentException(
                    "Pixel size must be either 8 or 32 bits.");
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
     *            image. Must be in the range 1..256.
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
            throw new NullPointerException();
        }
        image = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public DefineImage2 copy() {
        return new DefineImage2(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, width, height, pixelSize,
                tableSize, image.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 7;
        length += (pixelSize == 8) ? 1 : 0;
        length += image.length;

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_IMAGE_2 << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_IMAGE_2 << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);

        if (pixelSize == 8) {
            coder.writeWord(3, 1);
        } else { // 32
            coder.writeWord(5, 1);
        }

        coder.writeWord(width, 2);
        coder.writeWord(height, 2);

        if (pixelSize == 8) {
            coder.writeWord(tableSize - 1, 1);
        }

        coder.writeBytes(image);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
