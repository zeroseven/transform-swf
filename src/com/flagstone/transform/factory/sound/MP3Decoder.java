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

import com.flagstone.transform.coder.BigEndianDecoder;
import com.flagstone.transform.video.SoundFormat;

/**
 * Decoder for MP3 sounds so they can be added to a flash file.
 */
public final class MP3Decoder extends SoundDecoder
{
    protected static final int MPEG1 = 3;  
    protected static final int MP3_FRAME_SIZE[] = { 576, 576, 576, 1152 };
    protected static final int CHANNEL_COUNT[] = { 2, 2, 2, 1 };

    protected static final int BIT_RATES[][] =
    {
        { -1,  8, 16, 24, 32, 40, 48, 56,  64,  80,  96, 112, 128, 144, 160, -1 }, // MPEG 2.5
        { -1, -1, -1, -1, -1, -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, -1 }, // Reserved
        { -1,  8, 16, 24, 32, 40, 48, 56,  64,  80,  96, 112, 128, 144, 160, -1 }, // MPEG 2.0
        { -1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, -1 }, // MPEG 1.0
    };

    protected static final int SAMPLE_RATES[][] =
    {
        { 11025, -1, -1, -1 },
        {    -1, -1, -1, -1 },
        { 22050, -1, -1, -1 },
        { 44100, -1, -1, -1 }
    };
   
	@Override
	public SoundDecoder copy() {
		return new MP3Decoder();
	}
	
    @Override
	protected void decode(byte[] data) throws DataFormatException
    {
    	BigEndianDecoder coder = new BigEndianDecoder(data);
        
        int numberOfFrames = 0;
        int frameStart = 0;
        int[][] frameTable = null;

        format = SoundFormat.MP3;
        sampleSize = 2;
        
        int marker;
        
        while (!coder.eof())
        {        
        	marker = coder.readWord(3, false);
            coder.adjustPointer(-24);
        	
            if (marker == 0x494433) // ID3
            {
                coder.adjustPointer(24); // ID3 signature                
                coder.adjustPointer(8); // version number
                coder.adjustPointer(8); // revision number
                
                coder.adjustPointer(1); // unsynchronized
                coder.adjustPointer(1); // extendedHeader
                coder.adjustPointer(1); // experimental
                int hasFooter = coder.readBits(1, false);
                
                coder.adjustPointer(4);
                
                int totalLength = (hasFooter == 1) ? 10 : 0;

                totalLength += coder.readByte() << 21;
                totalLength += coder.readByte() << 14;
                totalLength += coder.readByte() << 7;
                totalLength += coder.readByte();

                coder.adjustPointer(totalLength<<3);
            }
            else if (marker == 0x544147) // ID3 V1
            {
                coder.adjustPointer(128<<3);
            }
            else if (coder.readBits(11, false) == 0x7FF) // MP3 frame
            {
            	coder.adjustPointer(-11);
            	
                if (numberOfFrames == 0) {
                    frameStart = coder.getPointer();
                }
                
                coder.adjustPointer(frameSize(coder) << 3);
                numberOfFrames++;
            }
            else
            {
                /*
                 * If we get here it means we jumped into the middle of either 
                 * a frame or tag information. This appears to be a common
                 * occurrence. Goto the end of the file so we can keep the 
                 * frames found so far.
                 */
                coder.setPointer(coder.getData().length<<3);
            }
        }

        int dataLength = coder.getData().length - (frameStart >> 3);
        
        sound = new byte[dataLength];
        
        System.arraycopy(coder.getData(), frameStart>>3, sound, 0, dataLength);
        
        frameTable = new int[numberOfFrames][2];
        
        for (int i=0; i<numberOfFrames; i++)
        {
            frameTable[i][0] = -1;
            frameTable[i][1] = 0;
        }

        coder.setPointer(frameStart);
        
        int frameNumber = 0;
        int samplesPerFrame;
        int version;
        
        while (coder.findBits(0x7FF, 11, 8))
        {
            frameTable[frameNumber][0] = (coder.getPointer()- frameStart) >> 3;
            
            coder.adjustPointer(11); // skip start of frame marker

            version = coder.readBits(2, false);
            
            samplesPerFrame = MP3_FRAME_SIZE[version];
            
            if (coder.readBits(2, false) != 1) {
                throw new DataFormatException("Flash only supports MPEG Layer 3");
            }
                
            coder.readBits(1, false); // crc follows header
            
            int bitRate = BIT_RATES[version][coder.readBits(4, false)];
            
            if (bitRate == -1) {
                throw new DataFormatException("Unsupported Bit-rate");
            }
            
            sampleRate = SAMPLE_RATES[version][coder.readBits(2, false)];
            
            if (sampleRate == -1) {
                throw new DataFormatException("Unsupported Sampling-rate");
            }
            
            int padding = coder.readBits(1, false);
            coder.readBits(1, false); // reserved

            numberOfChannels = CHANNEL_COUNT[coder.readBits(2, false)];

            coder.adjustPointer(6); // skip modeExtension, copyright, original and emphasis
            
            samplesPerChannel += samplesPerFrame;
            
            int frameSize = (((version == MPEG1) ? 144 : 72) * bitRate*1000 / sampleRate + padding) - 4;
            
            frameTable[frameNumber++][1] = 4 + frameSize;
    
            coder.adjustPointer(frameSize << 3);
        }
    }
    
	private int frameSize(BigEndianDecoder coder)
    {
        int frameSize = 4;
        
        coder.adjustPointer(11);

        int version = coder.readBits(2, false);
            
        coder.adjustPointer(3);
            
        int bitRate = BIT_RATES[version][coder.readBits(4, false)];
        int samplingRate = SAMPLE_RATES[version][coder.readBits(2, false)];
           int padding = coder.readBits(1, false);

        coder.adjustPointer(-23);
            
        frameSize += (((version == MPEG1) ? 144 : 72) * bitRate*1000 / samplingRate + padding) - 4;
        
        return frameSize;
    }
}
