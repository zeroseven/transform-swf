/*
 *  SoundConstructor.java
 *  Transform Utilities
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.util.sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.SoundFormat;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundStreamBlock;
import com.flagstone.transform.sound.SoundStreamHead2;
import com.flagstone.transform.util.image.BMPDecoder;
import com.flagstone.transform.util.image.ImageDecoder;
import com.flagstone.transform.util.image.ImageInfo;

/**
 * Decoder for MP3 sounds so they can be added to a flash file.
 */
public final class MP3Decoder implements SoundProvider, SoundDecoder
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
   
    private SoundFormat format;
    private int numberOfChannels;
    private int samplesPerChannel;
    private int sampleRate;
    private int sampleSize;
    private byte[] sound = null;

    public SoundDecoder newDecoder() {
    	return new MP3Decoder();
    }
    
    public void read(String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	read(new File(path));
    }
    
    public void read(File file) throws FileNotFoundException, IOException, DataFormatException
    {
		decode(loadFile(file));
    }

    public void read(URL url) throws FileNotFoundException, IOException, DataFormatException
    {
	    URLConnection connection = url.openConnection();

	    int fileSize = connection.getContentLength();
            
	    if (fileSize<0) {
              throw new FileNotFoundException(url.getFile());
	    }
	    
	    byte[] bytes = new byte[fileSize];

	    InputStream stream = url.openStream();
	    BufferedInputStream buffer = new BufferedInputStream(stream);

	    buffer.read(bytes);
	    buffer.close();

		decode(bytes);
    }

	/**
	 * Create a definition for an event sound using the sound in the specified file.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * sound in the Flash file.
	 * 
	 * @param file the File containing the abstract path to the sound.
	 * 
	 * @return a sound definition that can be added to a Movie.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the image, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the image.
	 */
    public DefineSound defineSound(int identifier)
    {
	    byte[] bytes = new byte[2+sound.length];
        bytes[0] = 0;
        bytes[1] = 0;
        System.arraycopy(sound, 0, bytes, 2, sound.length);

        return new DefineSound(identifier, format, sampleRate, numberOfChannels, sampleSize, samplesPerChannel, bytes);
    }

    /** 
     * Generates all the objects required to stream a sound from a file. 
     * 
     * @param frameRate the rate at which the movie is played. Sound are streamed
     * with one block of sound data per frame.
     * 
     * @param file the File containing the abstract path to the file containing 
     * the sound.
     * 
     * @return an array where the first object is the SoundStreamHead2 object 
     * that defines the streaming sound, followed by SoundStreamBlock objects 
     * containing the sound samples that will be played in each frame.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the sound, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the sound data.
     */
    public List<MovieTag> streamSound(int frameRate)
    {
     	ArrayList<MovieTag>array = new ArrayList<MovieTag>();
    	
    	int samplesPerBlock = sampleRate/frameRate;
	 	int numberOfBlocks = samplesPerChannel/samplesPerBlock;

	    int[][] frameTable = null;
	    int samplesPerFrame = 0;

	    array.add(new SoundStreamHead2(format, sampleRate, numberOfChannels, sampleSize, sampleRate, numberOfChannels, sampleSize, samplesPerBlock));
			
        int numberOfBytes = 0;        
        int framesToSend = 0;
        int framesSent = 0;
        int frameCount = 0;
        int sampleCount = 0;
        int seek = 0;
        
        SWFDecoder coder = new SWFDecoder(sound);
        
        coder.findBits(0x7FF, 11, 8);
        
        int frameStart = coder.getPointer();
        int numberOfFrames = 0;
        
        while (coder.findBits(0x7FF, 11, 8))
        {
            coder.adjustPointer(frameSize(coder) << 3);
            numberOfFrames++;
        }
        
        frameTable = new int[numberOfFrames][2];
        
        coder.setPointer(frameStart);
        
        int frameNumber = 0;
        
        while (coder.findBits(0x7FF, 11, 8))
        {
            frameTable[frameNumber][0] = (coder.getPointer()- frameStart + 16) >> 3;
            
            coder.adjustPointer(11); // skip start of frame marker

            int version = coder.readBits(2, false);
            
            coder.adjustPointer(3);
            
            int bitRate = BIT_RATES[version][coder.readBits(4, false)];
            int samplingRate = SAMPLE_RATES[version][coder.readBits(2, false)];
            int padding = coder.readBits(1, false);

            frameTable[frameNumber++][1] = 4 + (((version == MPEG1) ? 144 : 72) * bitRate*1000 / samplingRate + padding) - 4;
    
            coder.adjustPointer((frameSize(coder) << 3)-23);
        }

        for (int i=0; i<numberOfBlocks; i++)
	    {            	
            framesToSend = ((i+1) * samplesPerBlock) / samplesPerFrame;
            framesSent = (i * samplesPerBlock) / samplesPerFrame;
            frameCount = framesToSend - framesSent;
            sampleCount = frameCount * samplesPerFrame;
            seek = (i * samplesPerBlock) - (framesSent * samplesPerFrame);
        
            numberOfBytes = 4;
            
            for (int j=0, k=framesSent; j<frameCount; j++, k++) {
                 numberOfBytes += frameTable[k][1];
            }
            
            byte[] bytes = new byte[numberOfBytes];
        
            bytes[0] = (byte)sampleCount;
            bytes[1] = (byte)(sampleCount >> 8);
            bytes[2] = (byte)seek;
            bytes[3] = (byte)(seek >> 8);
            
            int offset = 4; 
            
            for (int j=0, k=framesSent; j<frameCount; j++, k++)
            {
                System.arraycopy(sound, frameTable[k][0], bytes, offset, frameTable[k][1]);
                offset += frameTable[k][1];
            }
            
            array.add(new SoundStreamBlock(bytes));
	    }
     	return array;
    }

    private int frameSize(SWFDecoder coder)
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

    private byte[] loadFile(final File file) throws FileNotFoundException, IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; //TODO(code) fix

		try {
			stream = new FileInputStream(file);
			int bytesRead = stream.read(data);

			if (bytesRead != data.length) {
				throw new IOException(file.getAbsolutePath());
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return data;
	}

	protected void decode(byte[] data) throws DataFormatException
    {
    	FLVDecoder coder = new FLVDecoder(data);
        
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
    
	private int frameSize(FLVDecoder coder)
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
