/*
 *  SoundConstructor.java
 *  Transform Utilities
 *
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.factory.sound;

import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.LittleEndianDecoder;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.video.SoundFormat;

/**
 * Decoder for WAV sounds so they can be added to a flash file.
 */
public final class WAVDecoder extends SoundDecoder
{
    protected static final int[] riffSignature = { 82, 73, 70, 70 };
    protected static final int[] wavSignature = { 87, 65, 86, 69 };

    protected static final int FMT = 0x20746d66;
    protected static final int DATA = 0x61746164;
    
	@Override
	public SoundDecoder copy() {
		return new WAVDecoder();
	}

	@Override
	protected void decode(byte[] data) throws DataFormatException
    {
    	LittleEndianDecoder coder = new LittleEndianDecoder(data);
        
        for (int i=0; i<4; i++)
        {
            if (coder.readByte() != riffSignature[i]) {
                throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }
        
        coder.readWord(4, false);
        
        for (int i=0; i<4; i++)
        {
            if (coder.readByte() != wavSignature[i]) {
                throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }
       
        int chunkType;
        int length;
        boolean moreChunks;

        do {
            chunkType = coder.readWord(4, false);
            length = coder.readWord(4, false);
            
            int blockStart = coder.getPointer();
            
            switch (chunkType)
            {
                case FMT: 
                	decodeFMT(coder); 
                	break;
                case DATA: 
                	decodeDATA(coder, length); 
                	break;
                default: 
                	coder.adjustPointer(length << 3); 
                	break;
            }

            int nextBlock = blockStart + (length << 3);
            coder.setPointer(nextBlock);
            moreChunks = !coder.eof();
        } while (moreChunks);
    }

    private void decodeFMT(LittleEndianDecoder coder) throws DataFormatException
    {
        format = SoundFormat.PCM;
        
        if (coder.readWord(2, false) != 1) {
            throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }
        
        numberOfChannels = coder.readWord(2, false);
        sampleRate = coder.readWord(4, false);
        coder.readWord(4, false); // total data length
        coder.readWord(2, false); // total bytes per sample
        sampleSize = coder.readWord(2, false) / 8;
    }
    
    private void decodeDATA(LittleEndianDecoder coder, int length)
    {
        samplesPerChannel = length / (sampleSize*numberOfChannels);

        sound = coder.readBytes(new byte[length]);
    }
}
