/*
 * ImageRegistry.java
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ImageRegistry is used to provide a directory for registering ImageProviders
 * that are used to decode different image formats.
 */
public final class ImageRegistry {

    /** The table of image providers used to decode each supported format. */
    private static Map<String, ImageProvider> providers =
        new LinkedHashMap<String, ImageProvider>();

    static {
        for (final ImageEncoding encoding : ImageEncoding.values()) {
            registerProvider(encoding.getMimeType(), encoding.getProvider());
        }
    }

    /**
     * Register an ImageDecoder to handle images in the specified format. The
     * image formats currently supported are defined in the ImageFormat
     * class.
     *
     * @param mimeType
     *            the string identifying the image format.
     * @param decoder
     *            any class that implements the ImageDecoder interface.
     */
    public static void registerProvider(final String mimeType,
            final ImageProvider decoder) {
        providers.put(mimeType, decoder);
    }

    /**
     * Get the provider that can be used to decode a given image format.
     * @param mimeType the MIME type identifying the image format.
     * @return an object implementing the ImageDecoder interface that can be
     * used to decode the image data.
     */
    public static ImageDecoder getImageProvider(final String mimeType) {

        if (providers.containsKey(mimeType)) {
            return providers.get(mimeType).newDecoder();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /** Private constructor for the image registry. */
    private ImageRegistry() {
        // Registry is shared.
    }
}
