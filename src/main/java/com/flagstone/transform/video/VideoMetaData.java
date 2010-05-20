/*
 * VideoData.java
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

import java.util.Arrays;

import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.FLVEncoder;
import com.flagstone.transform.coder.VideoTag;
import com.flagstone.transform.coder.VideoTypes;
import com.flagstone.transform.exception.IllegalArgumentRangeException;

/**
 * The VideoMetaData class is used to store information on how the video stream
 * should be displayed.
 *
 * <p>
 * Although meta-data can be found in all flash Video files there is no
 * documentation published by Adobe that describes the data structure. As a
 * result the information is decoded as a simple block of binary data.
 * </p>
 */
//TODO(class)
public final class VideoMetaData implements VideoTag {

    /** Format string used in toString() method. */
    private static final String FORMAT = "VideoMetaData: { data=%d }";

    private int timestamp;
    private byte[] data;

    /** The length of the object when it is encoded. */
    private transient int length;

    /**
     * Creates and initialises a VideoMetaData object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an FLVDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public VideoMetaData(final FLVDecoder coder) throws CoderException {
        final int start = coder.getPointer();
        coder.readByte();
        length = coder.readWord(3, false);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);
        timestamp = coder.readWord(3, false);
        coder.readUI32(); // reserved
        data = coder.readBytes(new byte[length]);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }

    /**
     * Constructs a new VideoMetaData object with the encoded data).
     *
     * @param time
     *            time in milliseconds from the start of the file where the
     *            meta-data is applied.
     * @param metaData
     *            an array of bytes containing the encoded meta-data. Must not
     *            be null.
     */
    public VideoMetaData(final int time, final byte[] metaData) {
        setTimestamp(time);
        setData(metaData);
    }

    /**
     * Creates and initialises a VideoMetaData object using the values copied
     * from another VideoMetaData object.
     *
     * @param object
     *            a VideoMetaData object from which the values will be
     *            copied.
     */
    public VideoMetaData(final VideoMetaData object) {
        timestamp = object.timestamp;
        data = object.data;
    }

    /**
     * Get the timestamp, in milliseconds, relative to the start of the
     * file, when the meta-data applies.
     *
     * @return the timestamp for the meta-data.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp, in milliseconds, relative to the start of the file,
     * when the audio or video will be played.
     *
     * @param time
     *            the time in milliseconds relative to the start of the file.
     *            Must be in the range 0..16,777,215.
     */
    public void setTimestamp(final int time) {
        if ((time < 0) || (time > 16777215)) {
            throw new IllegalArgumentRangeException(0, 16777215, time);
        }
        timestamp = time;
    }

    /**
     * Get a copy of the meta-data.
     *
     * @return a copy of the meta-data.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Sets the encoded meta data that describes how the video stream should be
     * played.
     *
     * @param metaData
     *            an array of bytes containing the encoded meta-data. Must not
     *            be null.
     */
    public void setData(final byte[] metaData) {
        if (metaData == null) {
            throw new IllegalArgumentException();
        }
        data = Arrays.copyOf(metaData, metaData.length);
    }

    /** {@inheritDoc} */
    public VideoMetaData copy() {
        return new VideoMetaData(this);
    }

    @Override
    /** {@inheritDoc} */
    public String toString() {
        return String.format(FORMAT, data.length);
    }

    /** {@inheritDoc} */
    public int prepareToEncode() {
        length = 11 + data.length;

        return length;
    }

    /** {@inheritDoc} */
    public void encode(final FLVEncoder coder) throws CoderException {
        final int start = coder.getPointer();

        coder.writeWord(VideoTypes.VIDEO_DATA, 1);
        coder.writeWord(length - 11, 3);
        final int end = coder.getPointer() + (length << Coder.BYTES_TO_BITS);
        coder.writeWord(timestamp, 3);
        coder.writeI32(0);
        coder.writeBytes(data);

        if (coder.getPointer() != end) {
            throw new CoderException(getClass().getName(),
                    start >> Coder.BITS_TO_BYTES, length,
                    (coder.getPointer() - end) >> Coder.BITS_TO_BYTES);
        }
    }
}
