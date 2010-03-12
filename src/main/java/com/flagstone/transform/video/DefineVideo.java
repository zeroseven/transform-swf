/*
 * DefineVideo.java
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

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.DefineTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * The DefineVideo class is used to define a video stream within a Flash file.
 *
 * <p>
 * Video objects contain a unique identifier and are treated in the same way as
 * shapes, buttons, images, etc. The video data displayed is define using the
 * VideoFrame class. Each frame of video is displayed whenever display list is
 * updated using the ShowFrame object - any timing information stored within the
 * video data is ignored. The actual video data is encoded using the VideoFrame
 * class.
 * </p>
 *
 * <p>
 * The ScreenVideo format was introduced in Flash 7, only the H263 format was
 * supported in Flash 6.
 * </p>
 *
 * @see VideoFrameType
 */
//TODO(class)
public final class DefineVideo implements DefineTag {

    private static final String FORMAT = "DefineVideo: { identifier=%d;"
            + " frameCount=%d; width=%d; height=%d; deblocking=%s;"
            + " smoothing=%s; codec=%s }";

    private int identifier;
    private int frameCount;
    private int width;
    private int height;
    private int deblocking;
    private boolean smoothing;
    private int codec;

    private transient int length;

    /**
     * Creates and initialises a DefineVideo object using values encoded in the
     * Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public DefineVideo(final SWFDecoder coder) throws CoderException {

        final int start = coder.getPointer();
        length = coder.readWord(2, false) & 0x3F;

        if (length == 0x3F) {
            length = coder.readWord(4, false);
        }
        final int end = coder.getPointer() + (length << 3);

        identifier = coder.readWord(2, true);
        frameCount = coder.readWord(2, false);
        width = coder.readWord(2, false);
        height = coder.readWord(2, false);

        coder.readBits(5, false);

        deblocking = coder.readBits(2, false);
        smoothing = coder.readBits(1, false) == 1;
        codec = coder.readByte();

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }

    /**
     * Creates a DefineVideo object with the specified parameters.
     *
     * @param uid
     *            the unique identifier for this object. Must be in the range
     *            1..65535.
     * @param count
     *            the number of video frames. Must be in the range 0..65535.
     * @param width
     *            the width of each frame in pixels. Must be in the range
     *            0..65535.
     * @param height
     *            the height of each frame in pixels. Must be in the range
     *            0..65535.
     * @param deblocking
     *            controls whether the Flash Player's deblocking filter is used,
     *            either Off, On or UseVideo to allow the video data to specify
     *            whether the deblocking filter is used.
     * @param smoothing
     *            turns smoothing on or off to improve the quality of the
     *            displayed image.
     * @param codec
     *            the format of the video data. Flash 6 supports H263. Support
     *            for Macromedia's ScreenVideo format was added in Flash 7.
     */
    public DefineVideo(final int uid, final int count, final int width,
            final int height, final Deblocking deblocking,
            final boolean smoothing, final VideoFormat codec) {
        setIdentifier(uid);
        setFrameCount(count);
        setWidth(width);
        setHeight(height);
        setDeblocking(deblocking);
        setSmoothing(smoothing);
        setCodec(codec);
    }

    /**
     * Creates and initialises an DefineVideo object using the values
     * copied from another DefineVideo object.
     *
     * @param object
     *            a DefineVideo object from which the values will be
     *            copied.
     */
    public DefineVideo(final DefineVideo object) {
        identifier = object.identifier;
        frameCount = object.frameCount;
        width = object.width;
        height = object.height;
        deblocking = object.deblocking;
        smoothing = object.smoothing;
        codec = object.codec;
    }

    /** TODO(method). */
    public int getIdentifier() {
        return identifier;
    }

    /** TODO(method). */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
             throw new IllegalArgumentRangeException(1, 65536, uid);
        }
        identifier = uid;
    }

    /**
     * Returns the number of frames in the video.
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Sets the number of frames in the video.
     *
     * @param count
     *            the number of video frames. Must be in the range 0..65535.
     */
    public void setFrameCount(final int count) {
        if ((count < 0) || (count > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, count);
        }
        frameCount = count;
    }

    /**
     * Returns the width of each frame in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of each frame in pixels.
     *
     * @param size
     *            the width of the frame. Must be in the range 0..65535.
     */
    public void setWidth(final int size) {
        if ((size < 0) || (size > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, size);
        }
        width = size;
    }

    /**
     * Returns the height of each frame in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of each frame in pixels.
     *
     * @param size
     *            the height of the frame. Must be in the range 0..65535.
     */
    public void setHeight(final int size) {
        if ((size < 0) || (size > 65535)) {
            throw new IllegalArgumentRangeException(0, 65535, size);
        }
        this.height = size;
    }

    /**
     * Returns the method used to control the Flash Player's deblocking filter,
     * either OFF, ON or USE_VIDEO.
     */
    public Deblocking getDeblocking() {
        Deblocking value;
        switch (deblocking) {
        case 1:
            value = Deblocking.OFF;
            break;
        case 2:
            value = Deblocking.ON;
            break;
        default:
            value = Deblocking.VIDEO;
            break;
        }
        return value;
    }

    /**
     * Sets the method used to control the Flash Player's deblocking filter.
     *
     * @param value
     *            the deblocking filter control, either OFF, ON or USE_VIDEO to
     *            allow the video data to specify whether the deblocking filter
     *            is used.
     */
    public void setDeblocking(final Deblocking value) {
        switch (value) {
        case VIDEO:
            deblocking = 0;
            break;
        case OFF:
            deblocking = 1;
            break;
        case ON:
            deblocking = 2;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns if the Flash Player will apply smoothing to the video when it is
     * played.
     */
    public boolean isSmoothed() {
        return smoothing;
    }

    /**
     * Sets whether Flash Player's smoothing filter is on or off when the video
     * is played.
     *
     * @param smoothing
     *            true if smoothing is turned on, false if it is turned off.
     */
    public void setSmoothing(final boolean smoothing) {
        this.smoothing = smoothing;
    }

    /**
     * Get the format used to encode the video data, either VideoFormat.H263 for
     * data encoded using the Sorenson modified H263 format or
     * VideoFormat.SCREEN (Flash 7 only) for data encoded using Macromedia's
     * Screen Video format.
     */
    public VideoFormat getCodec() {
        VideoFormat value;
        switch (codec) {
        case 2:
            value = VideoFormat.H263;
            break;
        case 3:
            value = VideoFormat.SCREEN;
            break;
        default:
            throw new IllegalStateException();
        }
        return value;
    }

    /**
     * Set the format used to encode the video data, either DefineVideo.H263 for
     * data encoded using the Sorenson modified H263 format or
     * DefineVideo.ScreenVideo (Flash 7 only) for data encoded using
     * Macromedia's Screen Video format.
     *
     * @param format
     *            the format used encode the video, either VideoFormat.H263 or
     *            VideoFormat.SCREEN.
     */
    public void setCodec(final VideoFormat format) {
        switch (format) {
        case H263:
            this.codec = 2;
            break;
        case SCREEN:
            this.codec = 3;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    /** {@inheritDoc} */
    public DefineVideo copy() {
        return new DefineVideo(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, identifier, frameCount, width, height,
                deblocking, smoothing, codec);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        length = 10;

        return 12;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        final int start = coder.getPointer();

        if (length >= 63) {
            coder.writeWord((MovieTypes.DEFINE_VIDEO << 6) | 0x3F, 2);
            coder.writeWord(length, 4);
        } else {
            coder.writeWord((MovieTypes.DEFINE_VIDEO << 6) | length, 2);
        }
        final int end = coder.getPointer() + (length << 3);

        coder.writeWord(identifier, 2);
        coder.writeWord(frameCount, 2);
        coder.writeWord(width, 2);
        coder.writeWord(height, 2);
        coder.writeBits(0, 5);
        coder.writeBits(deblocking, 2);
        coder.writeBits(smoothing ? 1 : 0, 1);
        coder.writeByte(codec);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(), start >> 3, length,
                    (coder.getPointer() - end) >> 3);
        }
    }
}
