/*
 * DefineJPEGImage3.java
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
 * DefineJPEGImage3 is used to define a transparent JPEG encoded image.
 *
 * <p>
 * It extends the DefineJPEGImage2 class by including a separate zlib compressed
 * table of alpha channel values. This allows the transparency of existing JPEG
 * encoded images to be changed without re-encoding the original image.
 * </p>
 *
 * @see DefineJPEGImage2
 */
public final class DefineJPEGImage3 implements ImageTag {

    private static final String FORMAT = "DefineJPEGImage3: { identifier=%d;"
            + "image=%d; alpha=%d }";

    private int identifier;
    private byte[] image;
    private byte[] alpha;

    private transient int length;
    private transient int width;
    private transient int height;

    /**
     * Creates and initialises a DefineJPEGImage3 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineJPEGImage3(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);
        identifier = coder.readUI16();

        final int offset = coder.readUI32();

        image = coder.readBytes(new byte[offset]);
        alpha = coder.readBytes(new byte[length - offset - 6]);

        decodeInfo();

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a DefineJPEGImage3 object with the specified image data, and
     * alpha channel data.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param img
     *            the JPEG encoded image data. Must not be null.
     * @param transparency
     *            byte array containing the zlib compressed alpha channel data.
     *            Must not be null.
     */
    public DefineJPEGImage3(final int uid, final byte[] img,
            final byte[] transparency) {
        setIdentifier(uid);
        setImage(img);
        setAlpha(transparency);
    }

    /**
     * Creates and initialises a DefineJPEGImage3 object using the values copied
     * from another DefineJPEGImage3 object.
     *
     * @param object
     *            a DefineJPEGImage3 object from which the values will be
     *            copied.
     */
    public DefineJPEGImage3(final DefineJPEGImage3 object) {
        identifier = object.identifier;
        width = object.width;
        height = object.height;
        image = object.image;
        alpha = object.alpha;
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
     * Returns a copy of the image data.
     */
    public byte[] getImage() {
        return Arrays.copyOf(image, image.length);
    }

    /**
     * Returns  a copy of the alpha channel data.
     */
    public byte[] getAlpha() {
        return Arrays.copyOf(alpha, alpha.length);
    }

    /**
     * Sets the image data.
     *
     * @param bytes
     *            an array of bytes containing the image table. Must not be
     *            null.
     */
    public void setImage(final byte[] bytes) {
        image = Arrays.copyOf(bytes, bytes.length);
        decodeInfo();
    }

    /**
     * Sets the alpha channel data with the zlib compressed data.
     *
     * @param bytes
     *            array of bytes containing zlib encoded alpha channel. Must not
     *            be null.
     */
    public void setAlpha(final byte[] bytes) {
        alpha = Arrays.copyOf(bytes, bytes.length);
    }

    /** {@inheritDoc} */
    public DefineJPEGImage3 copy() {
        return new DefineJPEGImage3(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, image.length, alpha.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final Context context) {
        length = 6;
        length += image.length;
        length += alpha.length;

        return (length > SWFEncoder.STD_LIMIT ? SWFEncoder.EXT_LENGTH
                : SWFEncoder.STD_LENGTH) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_JPEG_IMAGE_3, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeI16(identifier);
        coder.writeI32(image.length);
        coder.writeBytes(image);
        coder.writeBytes(alpha);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    private void decodeInfo() {
        final JPEGInfo info = new JPEGInfo();
        info.decode(image);
        width = info.getWidth();
        height = info.getHeight();
    }
}
