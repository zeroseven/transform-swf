/*
 * DefineJPEGImage2.java
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
 * DefineJPEGImage2 is used to define a JPEG encoded image with an integrated
 * encoding table.
 *
 * @see DefineJPEGImage
 * @see DefineJPEGImage3
 */
public final class DefineJPEGImage2 implements ImageTag {

    private static final String FORMAT = "DefineJPEGImage2: { identifier=%d;"
            + " image=%d; }";

    private int identifier;
    private byte[] image;

    private transient int length;
    private transient int width;
    private transient int height;

    /**
     * Creates and initialises a DefineJPEGImage2 object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineJPEGImage2(final SWFDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        length = coder.readHeader();
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        identifier = coder.readUI16();
        image = coder.readBytes(new byte[length - 2]);

        decodeInfo();

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Creates a DefineJPEGImage2 object with the identifier and JPEG
     * image data.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param img
     *            the JPEG encoded image data. Must not be null.
     */
    public DefineJPEGImage2(final int uid, final byte[] img) {
        setIdentifier(uid);
        setImage(img);
    }

    /**
     * Creates and initialises a DefineJPEGImage2 object using the values copied
     * from another DefineJPEGImage2 object.
     *
     * @param object
     *            a DefineJPEGImage2 object from which the values will be
     *            copied.
     */
    public DefineJPEGImage2(final DefineJPEGImage2 object) {
        identifier = object.identifier;
        width = object.width;
        height = object.height;
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
     * Sets the image data.
     *
     * @param bytes
     *            an array of bytes containing the image data. Must not be null.
     */
    public void setImage(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        image = Arrays.copyOf(bytes, bytes.length);
        decodeInfo();
    }

    /** {@inheritDoc} */
    public DefineJPEGImage2 copy() {
        return new DefineJPEGImage2(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, image.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        length = 2 + image.length;

        return (length > 62 ? 6 : 2) + length;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        final int start = coder.getPointer();
        coder.writeHeader(MovieTypes.DEFINE_JPEG_IMAGE_2, length);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);

        coder.writeWord(identifier, 2);
        coder.writeBytes(image);

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
