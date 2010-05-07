/*
 * SoundFormat.java 
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
package com.flagstone.transform.sound;

/**
 * SoundFormat is used to identify the different encoding formats used for event
 * and streaming sounds in Flash and Flash Video files.
 */
public enum SoundFormat {
    /**
     * Uncompressed Pulse Code Modulated: samples are either 1 or 2 bytes. For
     * two-byte samples the byte order is dependent on the platform on which the
     * Flash Player is hosted. Sounds created on a platform which support
     * big-endian byte order will not be played correctly when listened to on a
     * platform which supports little-endian byte order.
     */
    NATIVE_PCM,
    /**
     * Compressed ADaptive Pulse Code Modulated: samples are encoded and
     * compressed by comparing the difference between successive sound sample
     * which dramatically reduces the size of the encoded sound when compared to
     * the uncompressed PCM formats. Use this format or MP3 whenever possible.
     */
    ADPCM,
    /**
     * Compressed MPEG Audio Layer-3.
     * */
    MP3,
    /**
     * Uncompressed pulse code modulated sound. Samples are either 1 or 2 bytes.
     * The byte ordering for 16-bit samples is little-endian.
     */
    PCM,
    /**
     * Compressed Nellymoser Asao format for a mono sound played at 8KHz
     * supporting low bit-rate sound for improved synchronisation between the
     * sound and frame rate of movies. This format is not supported in SWF
     * files, only in Flash Video files.
     */
    NELLYMOSER_8K,
    /**
     * Compressed Nellymoser Asao format supporting low bit-rate sound for
     * improved synchronisation between the sound and frame rate of movies. This
     * format is for mono sounds.
     */
    NELLYMOSER,
    /**
     * 
     */
    SPEEX;
}
