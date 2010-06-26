/*
 * ImageBlock.java
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

package com.flagstone.transform.util.image;

import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

import com.flagstone.transform.video.ImageBlock;

/**
 * ImageBlocker is used to sub-divide an image into a set of blocks so they can
 * be streamed using Screen Video. Image blocks are compared so only pixel
 * information for the portions of the image that change are sent.
 *
 * @see ImageBlock
 */
public final class ImageBlocker {

    /** Number of colour channels in an RGB pixel. */
    private static final int RGB_CHANNELS = 3;

    /**
     * Return an image stored in a a file as a list of ImageBlock objects that
     * can be used when creating ScreenVideo streams.
     *
     * The image is divided by tiling blocks of the specified width and height
     * across the image. For blocks at the right and bottom edges the size of
     * the block may be reduced so that it fits the image exactly. In other
     * words the blocks are not padded with extra pixel information.
     *
     * @param  blocks
     *            a list of ImageBlock objects
     * @param blockWidth
     *            the width of a block in pixels
     * @param blockHeight
     *            the height of a block in pixels
     * @param imageWidth
     *            the width of the image in pixels
     * @param imageHeight
     *            the height of the image in pixels
     * @param image
     *            the image data
     */
    public void getImageAsBlocks(final List<ImageBlock> blocks,
            final int blockWidth, final int blockHeight,
            final int imageWidth, final int imageHeight,
            final byte[] image) {

        final ImageFilter filter = new ImageFilter();
        byte[] img = filter.removeAlpha(image);
        img = filter.invertRGB(img, imageWidth, imageHeight);
        filter.reverseRGB(img);

        final int columns = (imageWidth + blockWidth - 1) / blockWidth;
        final int rows = (imageHeight + blockHeight - 1) / blockHeight;

        final byte[] blockData = new byte[blockHeight * blockWidth
                                          * RGB_CHANNELS];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                final int xOffset = j * blockWidth;
                final int yOffset = i * blockHeight;

                final int xSpan = (imageWidth - xOffset > blockWidth)
                        ? blockWidth : imageWidth - xOffset;
                final int ySpan = (imageHeight - yOffset > blockHeight)
                        ? blockHeight : imageHeight - yOffset;
                int offset = 0;

                int idx;

                for (int k = 0; k < ySpan; k++) {
                    for (int l = 0; l < xSpan; l++, offset += RGB_CHANNELS) {
                        idx = (yOffset + k) * (imageWidth * RGB_CHANNELS)
                                + (xOffset + l) * RGB_CHANNELS;

                        blockData[offset] = img[idx];
                        blockData[offset + 1] = img[idx + 1];
                        blockData[offset + 2] = img[idx + 2];
                    }
                }

                blocks.add(new ImageBlock(xSpan, ySpan,
                        zip(blockData, offset)));
            }
        }
    }

    /**
     * Compress the image using the ZIP format.
     * @param image the image data.
     * @param length the number of bytes from the image to compress.
     * @return the compressed image.
     */
    private byte[] zip(final byte[] image, final int length) {
        final Deflater deflater = new Deflater();
        deflater.setInput(image, 0, length);
        deflater.finish();

        final byte[] compressedData = new byte[image.length];
        final int bytesCompressed = deflater.deflate(compressedData);
        final byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);

        return newData;
    }
}
