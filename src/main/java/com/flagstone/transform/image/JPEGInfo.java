/*
 * JPEGInfo.java
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

package com.flagstone.transform.image;

import com.flagstone.transform.coder.BigDecoder;
import com.flagstone.transform.coder.Coder;

/**
 * JPEGInfo is used to extract the width and height from a JPEG encoded image.
 */
public final class JPEGInfo {
    /** The width of the image. */
    private transient int width;
    /** The height of the image. */
    private transient int height;

    /**
     * Get the width of the image in pixels, not twips.
     *
     * @return the width of the image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the height of the image in pixels, not twips.
     *
     * @return the height of the image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Decode a JPEG encoded image.
     *
     * @param image the image data.
     */
    public void decode(final byte[] image) {
        final BigDecoder coder = new BigDecoder(image);
        int marker;
        int size;
        while (!coder.eof()) {
            marker = coder.readUI16();
            if (marker == 0xffd8 || marker == 0xffd9) {
                continue;
            }
            size = coder.readUI16();
            if (marker >= 0xffc0 && marker <= 0xffcf
                    && marker != 0xffc4 && marker != 0xffc8) {
                coder.readWord(1, false);
                height = coder.readUI16();
                width = coder.readUI16();
                return;
            } else {
                coder.adjustPointer((size - 2) << Coder.BYTES_TO_BITS);
            }
        }
    }
}
