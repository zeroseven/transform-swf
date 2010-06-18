/*
 * SoundEncoding.java
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

package com.flagstone.transform.util.sound;

/**
 * SoundEncoding describes the different sound formats that can be decoded and
 * added to a Flash movie.
 */
enum SoundEncoding {
    /** MPEG Version 3 (MP3) format. */
    MP3("audio/mpeg", new MP3Decoder()),
    /** Waveform Audio File Format. */
    WAV("audio/x-wav", new WAVDecoder());

    /** The MIME type used to identify the sound format. */
    private final String mimeType;
    /** The SoundProvider that can be used to decode the sound format. */
    private final SoundProvider provider;

    /**
     * Private constructor for the enum.
     *
     * @param type the string representing the mime-type.
     * @param soundProvider the SoundProvider that can be used to decode the
     * sound format.
     */
    private SoundEncoding(final String type,
            final SoundProvider soundProvider) {
        mimeType = type;
        provider = soundProvider;
    }

    /**
     * Get the mime-type used to represent the sound format.
     *
     * @return the string identifying the sound format.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Get the SoundProvider that can be registered in the SoundRegistry to
     * decode the sound.
     *
     * @return the SoundProvider that can be used to decode sounds of the given
     * mime-type.
     */
    public SoundProvider getProvider() {
        return provider;
    }
}
