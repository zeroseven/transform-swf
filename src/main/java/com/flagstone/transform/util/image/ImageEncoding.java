/*
 * ImageEncoding.java
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

package com.flagstone.transform.util.image;

/**
 * ImageEncoding describes the different image formats that can be decoded and
 * added to a Flash movie.
 */
public enum ImageEncoding {
    /** Windows Bitmap images. */
    BMP("image/bmp", new BMPDecoder()),
    /** Graphics Interchange Format images. */
    GIF("image/gif", new BufferedImageDecoder()),
    /** Interchange File Format images. */
    IFF("image/iff", new BufferedImageDecoder()),
    /** Joint Photographic Experts Group format images. */
    JPEG("image/jpeg", new JPGDecoder()),
    /** Portable Bitmap images. */
    PBM("image/x-portable-bitmap", new BufferedImageDecoder()),
    /** Pacific Exchange (ZSoft) images. */
    PCX("image/pcx", new BufferedImageDecoder()),
    /** Portable Gray Map images. */
    PGM("image/x-portable-pixmap", new BufferedImageDecoder()),
    /** Portable Network Graphics images. */
    PNG("image/png", new PNGDecoder()),
    /** Photoshop Document images. */
    PSD("image/psd", new BufferedImageDecoder()),
    /** Raster images. */
    RAS("image/ras", new BufferedImageDecoder());

    /** The MIME type used to identify the image format. */
    private final String mimeType;
    /** The ImageProvider that can be used to decode the image format. */
    private final ImageProvider provider;

    /**
     * Private constructor for the enum.
     *
     * @param type the string representing the mime-type.
     * @param imageProvider the ImageProvider that can be used to decode the
     * image format.
     */
    private ImageEncoding(final String type,
            final ImageProvider imageProvider) {
        mimeType = type;
        provider = imageProvider;
    }

    /**
     * Get the mime-type used to represent the image format.
     *
     * @return the string identifying the image format.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Get the ImageProvider that can be registered in the ImageRegistry to
     * decode the image.
     *
     * @return the ImageProvider that can be used to decode images of the given
     * mime-type.
     */
    public ImageProvider getProvider() {
        return provider;
    }
}
