/*
 * ScreenVideoPacket.java
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
package com.flagstone.transform.video;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * The ScreenVideoPacket class is used to encode or decode a frame of video data
 * using Macromedia's ScreenVideo format.
 */
public final class ScreenPacket implements Cloneable {
    private boolean keyFrame;
    private int blockWidth;
    private int blockHeight;
    private int imageWidth;
    private int imageHeight;
    private List<ImageBlock> imageBlocks;


    public ScreenPacket(final byte[] data) {
        final SWFDecoder coder = new SWFDecoder(data);

        keyFrame = coder.readBits(4, false) == 1;
        coder.readBits(4, false); // codec = screen_video

        blockWidth = (coder.readBits(4, false) + 1) * 16;
        imageWidth = coder.readBits(12, false);
        blockHeight = (coder.readBits(4, false) + 1) * 16;
        imageHeight = coder.readBits(12, false);

        final int columns = imageWidth / blockWidth
                + ((imageWidth % blockWidth > 0) ? 1 : 0);
        final int rows = imageHeight / blockHeight
                + ((imageHeight % blockHeight > 0) ? 1 : 0);

        int height = imageHeight;
        int width = imageWidth;

        imageBlocks.clear();
        ImageBlock block;

        for (int i = 0; i < rows; i++, height -= blockHeight) {
            for (int j = 0; j < columns; j++, width -= blockWidth) {
                final int length = coder.readBits(16, false);

                if (length == 0) {
                    block = new ImageBlock(0, 0, null);
                } else {
                    final int dataHeight = (height < blockHeight) ? height
                            : blockHeight;
                    final int dataWidth = (width < blockWidth) ? width
                            : blockWidth;

                    block = new ImageBlock(dataHeight, dataWidth, coder
                            .readBytes(new byte[length]));
                }

                imageBlocks.add(block);
            }
        }
    }


    public ScreenPacket() {
        imageBlocks = new ArrayList<ImageBlock>();
    }

    /**
     * Creates a ScreenVideoPacket.
     *
     * @param key
     *            indicates whether the packet contains a key frame.
     * @param imgWidth
     *            the width of the frame.
     * @param imgHeight
     *            the height of the frame.
     * @param blkWidth
     *            the width of the blocks that make up the frame.
     * @param blkHeight
     *            the height of the blocks that make up the frame.
     * @param blocks
     *            the array of ImageBlocks that make up the frame.
     */
    public ScreenPacket(final boolean key, final int imgWidth,
            final int imgHeight, final int blkWidth, final int blkHeight,
            final List<ImageBlock> blocks) {
        setKeyFrame(key);
        setImageWidth(imgWidth);
        setImageHeight(imgHeight);
        setBlockWidth(blkWidth);
        setBlockHeight(blkHeight);
        setImageBlocks(blocks);
    }

    /**
     * Creates and initialises a ScreenPacket object using the values copied
     * from another ScreenPacket object.
     *
     * @param object
     *            a ScreenPacket object from which the values will be
     *            copied.
     */
    public ScreenPacket(final ScreenPacket object) {
        keyFrame = object.keyFrame;
        blockWidth = object.blockWidth;
        blockHeight = object.blockHeight;
        imageWidth = object.imageWidth;
        imageHeight = object.imageHeight;

        imageBlocks = new ArrayList<ImageBlock>(object.imageBlocks.size());

        for (final ImageBlock block : object.imageBlocks) {
            imageBlocks.add(block.copy());
        }
    }

    /**
     * Add an image block to the array that make up the frame.
     *
     * @param block
     *            an ImageBlock. Must not be null.
     */
    public ScreenPacket add(final ImageBlock block) {
        imageBlocks.add(block);
        return this;
    }

    /**
     * Returns true if the packet contains a key frame.
     */
    public boolean isKeyFrame() {
        return keyFrame;
    }

    /**
     * Sets whether the frame is a key frame (true) or normal one (false).
     *
     * @param key
     *            a boolean value indicating whether the frame is key (true) or
     *            normal (false.
     */
    public void setKeyFrame(final boolean key) {
        keyFrame = key;
    }

    /**
     * Returns the width of the frame in pixels.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets the width of the frame.
     *
     * @param width
     *            the width of the frame in pixels.
     */
    public void setImageWidth(final int width) {
        imageWidth = width;
    }

    /**
     * Returns the height of the frame in pixels.
     */
    public int getImageHeight() {
        return imageHeight;
    }


    public void setImageHeight(final int height) {
        imageHeight = height;
    }

    /**
     * Returns the width of the blocks in pixels.
     */
    public int getBlockWidth() {
        return blockWidth;
    }

    /**
     * Sets the width of the image blocks.
     *
     * @param width
     *            the width of the blocks in pixels.
     */
    public void setBlockWidth(final int width) {
        blockWidth = width;
    }

    /**
     * Returns the height of the blocks in pixels.
     */
    public int getBlockHeight() {
        return blockHeight;
    }

    /**
     * Sets the height of the image blocks.
     *
     * @param height
     *            the height of the blocks in pixels.
     */
    public void setBlockHeight(final int height) {
        blockHeight = height;
    }

    /**
     * Returns the image blocks that have changed in this frame.
     */
    public List<ImageBlock> getImageBlocks() {
        return imageBlocks;
    }

    /**
     * Set the image blocks that have changed in this frame. If this is a key
     * frame then all image blocks are displayed.
     *
     * @param blocks
     *            the array of image blocks. Must not be null.
     */
    public void setImageBlocks(final List<ImageBlock> blocks) {
        imageBlocks = new ArrayList<ImageBlock>(blocks);
    }

    /** {@inheritDoc} */
    public ScreenPacket copy() {
        return new ScreenPacket(this);
    }

    private int length() {
        int length = 5;

        for (final ImageBlock block : imageBlocks) {
            length += 2;

            if (!block.isEmpty()) {
                length += block.getBlock().length;
            }
        }
        return length;
    }


    public byte[] encode() {
        final SWFEncoder coder = new SWFEncoder(length());

        coder.writeBits(keyFrame ? 1 : 2, 4);
        coder.writeBits(3, 4);

        coder.writeBits((blockWidth / 16) - 1, 4);
        coder.writeBits(imageWidth, 12);
        coder.writeBits((blockHeight / 16) - 1, 4);
        coder.writeBits(imageHeight, 12);

        byte[] blockData;

        for (final ImageBlock block : imageBlocks) {
            if (block.isEmpty()) {
                coder.writeWord(0, 2);
            } else {
                blockData = block.getBlock();
                coder.writeBits(blockData.length, 16);
                coder.writeBytes(blockData);
            }
        }

        return coder.getData();
    }
}
