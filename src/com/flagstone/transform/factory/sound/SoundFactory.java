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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.LittleEndianDecoder;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.sound.DefineSound;
import com.flagstone.transform.movie.sound.SoundStreamBlock;
import com.flagstone.transform.movie.sound.SoundStreamHead2;
import com.flagstone.transform.video.SoundFormat;

/** 
 * The Sound class is used to generate the objects used to define and control
 * the sounds that are played in a Flash movie. Sound can be used to generate 
 * definitions for:
 *
 * <ul>
 * <li>Event sounds that are played in response to a particular event such as a 
 * button being clicked.</li>
 * <li>Streaming sound that is played as movie is being displayed.</li>
 * </ul>
 *
 * The class supports a plug-in architecture allows decoders to be registered to 
 * handle different sound formats.</p>
 * 
 * <p>Currently WAV, MP3 format sounds are supported using the decoders provided 
 * in the Transform framework. New decoders can be added by implementing
 * the SoundDecoder interface and registering them using the registerDecoder()
 * method.</p>
 * 
 * Once loaded, PCM sound data should be compressed to ADPCM format as the relatively 
 * large sizes make PCM coded sounds generally unsuitable for inclusion in a Flash 
 * movie. ADPCM is a compressed format and Flash supports a modified ADPCM algorithm 
 * with compressed samples taking 2, 3, 4 or 5 bits. This results in much smaller 
 * file sizes when a movie is encoded. Code, developed at Centre for Mathematics 
 * and Computer Science, Amsterdam, The Netherlands, is available on Flagstone's 
 * web site to perform the ADPCM compression.
 * 
 * For sounds containing more than one channel, the sound levels for each channel 
 * are interleaved for each sample. For example a stereo sound the order of the 
 * samples and channels levels are:
 *
 *<pre>
 * Sample       0          1          2
 *          +---+---+  +---+---+  +---+---+
 *          | 1 | 2 |  | 1 | 2 |  | 1 | 2 | ....
 *          +---+---+  +---+---+  +---+---+
 *</pre>
 *
 * NOTE: The byte order for the NATIVE_PCM data in WAV sound files may vary 
 * according to the platform on which the sound file was created. Sound currently 
 * only supports WAV files with little-endian byte order.
 *
 */
public final class SoundFactory
{  
    private static final int MPEG1 = 3;  

    private static final int BIT_RATES[][] =
    {
        { -1,  8, 16, 24, 32, 40, 48, 56,  64,  80,  96, 112, 128, 144, 160, -1 }, // MPEG 2.5
        { -1, -1, -1, -1, -1, -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, -1 }, // Reserved
        { -1,  8, 16, 24, 32, 40, 48, 56,  64,  80,  96, 112, 128, 144, 160, -1 }, // MPEG 2.0
        { -1, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, -1 }, // MPEG 1.0
    };

    private static final int SAMPLE_RATES[][] =
    {
        { 11025, -1, -1, -1 },
        {    -1, -1, -1, -1 },
        { 22050, -1, -1, -1 },
        { 44100, -1, -1, -1 }
    };
    
	private static Map<String, SoundDecoder> decoders = new LinkedHashMap<String, SoundDecoder>();

	static {
		registerDecoder("wav", new WAVDecoder());
		registerDecoder("mp3", new MP3Decoder());
	}
	
	/**
	 * Register a SoundDecoder to handle images in the specified format. The 
	 * formats currently supported are WAV and MP3.
	 * 
	 * @param format the string identifying the sound format. 
	 * @param obj any class that implements the SoundDecoder interface.
	 */
	public static void registerDecoder(String format, SoundDecoder obj)
	{
		decoders.put(format.toLowerCase(Locale.getDefault()), obj);
	}

	/**
	 * Create a definition for an event sound using the sound located at the specified path.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * sound in the Flash file.
	 * 
	 * @param path the path to the file containing the image.
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
    public static DefineSound defineSound(int identifier, String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	return defineSound(identifier, new File(path));
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
    public static DefineSound defineSound(int identifier, File file) throws FileNotFoundException, IOException, DataFormatException
    {
    	DefineSound object = null;

    	String name = file.getName();
    	String extension = name.substring(name.lastIndexOf('.')+1, name.length());
    	
    	if (decoders.containsKey(extension))
    	{
			SoundDecoder decoder = decoders.get(extension).copy();
			decoder.decode(loadFile(file));
			
			SoundFormat format = decoder.getFormat();
		    int channels = decoder.getNumberOfChannels();
		    int samples = decoder.getSamplesPerChannel();
		    int rate = decoder.getSampleRate();
		    int size = decoder.getSampleSize();
		    byte[] sound = decoder.getSound();
		    byte[] bytes = null;

	        switch (format)
	        {
	            case PCM:
	            case ADPCM:
	                bytes = new byte[sound.length];
	                System.arraycopy(sound, 0, bytes, 0, sound.length);
	                break;
	            case MP3:
	                bytes = new byte[2+sound.length];
	                bytes[0] = 0;
	                bytes[1] = 0;
	                System.arraycopy(sound, 0, bytes, 2, sound.length);
	                break;
	            default:
	            	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
	        }
	        object = new DefineSound(identifier, format, rate, channels, size, samples, bytes);
    	}
    	return object;
    }
    
	/**
	 * Create a definition for an event sound using the file referenced by a URL.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * sound in the Flash file.
	 * 
	 * @param url the Uniform Resource Locator referencing the file.
	 * 
	 * @return a sound definition that can be added to a Movie.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the sound, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the sound data.
	 */
    public static DefineSound defineSound(int identifier, URL url) throws FileNotFoundException, IOException, DataFormatException
    {
    	DefineSound object = null;
     	String extension = null;  	
	    byte[] data = SoundFactory.loadURL(url);

    	if (decoders.containsKey(extension))
    	{
			SoundDecoder obj = decoders.get(extension);
			SoundDecoder decoder = obj.copy();
			decoder.decode(data);
			
			SoundFormat format = decoder.getFormat();
		    int channels = decoder.getNumberOfChannels();
		    int samples = decoder.getSamplesPerChannel();
		    int rate = decoder.getSampleRate();
		    int size = decoder.getSampleSize();
		    byte[] sound = decoder.getSound();
		    byte[] bytes = null;

	        switch (format)
	        {
	            case PCM:
	            case ADPCM:
	                bytes = new byte[sound.length];
	                System.arraycopy(sound, 0, bytes, 0, sound.length);
	                break;
	            case MP3:
	                bytes = new byte[2+sound.length];
	                bytes[0] = 0;
	                bytes[1] = 0;
	                System.arraycopy(sound, 0, bytes, 2, sound.length);
	                break;
	            default:
	            	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
	        }
	        object = new DefineSound(identifier, format, rate, channels, size, samples, bytes);
	}
    	return object;
    }
    
    /** 
     * Generates all the objects required to generate a streaming sound from 
     * a file. 
     * 
     * @param frameRate the rate at which the movie is played. Sound are streamed
     * with one block of sound data per frame.
     * 
     * @param path the path to the file containing the sound.
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
    public static List<MovieTag> streamSound(int frameRate, String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	return streamSound(frameRate, new File(path));
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
    public static List<MovieTag> streamSound(int frameRate, File file) throws FileNotFoundException, IOException, DataFormatException
    {
    	String extension = null;
    	ArrayList<MovieTag>array = new ArrayList<MovieTag>();
    	
    	if (decoders.containsKey(extension))
    	{
			SoundDecoder obj = decoders.get(extension);
			SoundDecoder decoder = obj.copy();
			decoder.decode(loadFile(file));
			
	        int firstSample = 0;
	        int firstSampleOffset = 0;
	        int bytesPerBlock = 0;
	        int bytesRemaining = 0;
	        int numberOfBytes = 0;
	        
	        int framesToSend = 0;
	        int framesSent = 0;
	        int frameCount = 0;
	        int sampleCount = 0;
	        int seek = 0;
	        
	        SoundFormat format = decoder.getFormat();
		    int channels = decoder.getNumberOfChannels();
		    int samples = decoder.getSamplesPerChannel();
		    int rate = decoder.getSampleRate();
		    int size = decoder.getSampleSize();
		    byte[] sound = decoder.getSound();
		    byte[] bytes = null;
		    
	    	int samplesPerBlock = rate/frameRate;
		 	int numberOfBlocks = samples/samplesPerBlock;

		    int[][] frameTable = null;
		    int samplesPerFrame = 0;

		    array.add(new SoundStreamHead2(format, rate, channels, size, rate, channels, size, samplesPerBlock));

		 	switch (format)
	        {
	            case PCM:
	        	    for (int i=0; i<numberOfBlocks; i++)
	        	    {
		                firstSample = i*samplesPerBlock;
		                firstSampleOffset = firstSample * size * channels;
		                bytesPerBlock = samplesPerBlock * size * channels;
		                bytesRemaining = sound.length - firstSampleOffset;
		                
		                numberOfBytes = (bytesRemaining < bytesPerBlock) ? bytesRemaining : bytesPerBlock;
		            
		                bytes = new byte[numberOfBytes];
		                System.arraycopy(sound, firstSampleOffset, bytes, 0, numberOfBytes);
		                
		                array.add(new SoundStreamBlock(bytes));
	        	    }
	                break;
	            case ADPCM:
	                break;
	            case MP3:
	                LittleEndianDecoder coder = new LittleEndianDecoder(bytes);
	                
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
		                
		                bytes = new byte[numberOfBytes];
		            
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
	                break;
	            default:
	            	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
		    }
    	}
    	return array;
    }

    /** 
     * Generates all the objects required to generate a streaming sound from 
     * a URL reference. 
     * 
     * @param frameRate the rate at which the movie is played. Sound are streamed
     * with one block of sound data per frame.
     * 
 	 * @param url the Uniform Resource Locator referencing the file containing
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
    public static List<MovieTag> streamSound(int frameRate, URL url) throws IOException, DataFormatException
    {
    	String extension = null;
    	ArrayList<MovieTag>array = new ArrayList<MovieTag>();
   	    byte[] data = SoundFactory.loadURL(url);

    	if (decoders.containsKey(extension))
    	{
			SoundDecoder obj = decoders.get(extension);
			SoundDecoder decoder = obj.copy();
			decoder.decode(data);
			
	        int firstSample = 0;
	        int firstSampleOffset = 0;
	        int bytesPerBlock = 0;
	        int bytesRemaining = 0;
	        int numberOfBytes = 0;
	        
	        int framesToSend = 0;
	        int framesSent = 0;
	        int frameCount = 0;
	        int sampleCount = 0;
	        int seek = 0;
	        
    	    SoundFormat format = decoder.getFormat();
		    int channels = decoder.getNumberOfChannels();
		    int samples = decoder.getSamplesPerChannel();
		    int rate = decoder.getSampleRate();
		    int size = decoder.getSampleSize();
		    byte[] sound = decoder.getSound();
		    byte[] bytes = null;
		    
	    	int samplesPerBlock = rate/frameRate;
		 	int numberOfBlocks = samples/samplesPerBlock;

		    int[][] frameTable = null;
		    int samplesPerFrame = 0;

		    array.add(new SoundStreamHead2(format, rate, channels, size, rate, channels, size, samplesPerBlock));

		 	switch (format)
	        {
	            case PCM:
	        	    for (int i=0; i<numberOfBlocks; i++)
	        	    {
		                firstSample = i*samplesPerBlock;
		                firstSampleOffset = firstSample * size * channels;
		                bytesPerBlock = samplesPerBlock * size * channels;
		                bytesRemaining = sound.length - firstSampleOffset;
		                
		                numberOfBytes = (bytesRemaining < bytesPerBlock) ? bytesRemaining : bytesPerBlock;
		            
		                bytes = new byte[numberOfBytes];
		                System.arraycopy(sound, firstSampleOffset, bytes, 0, numberOfBytes);
		                
		                array.add(new SoundStreamBlock(bytes));
	        	    }
	                break;
	            case ADPCM:
	                break;
	            case MP3:
	            	LittleEndianDecoder coder = new LittleEndianDecoder(bytes);
	                
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
	                    frameTable[frameNumber][0] = (coder.getPointer()- frameStart) >> 3;
	                    
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
		                
		                bytes = new byte[numberOfBytes];
		            
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
	                break;
	            default:
	            	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
		    }
    	}
    	return array;
     }

    private static int frameSize(LittleEndianDecoder coder)
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

	/**
	 * Reads an array of bytes from a URL.
	 * 
	 * @param url
	 *            the path to the file.
	 * @return the contents of the file.
	 * 
	 * @throws FileNotFoundException
	 *             if the URL cannot be opened for reading.
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the data.
	 */
	public static byte[] loadURL(final URL url) throws FileNotFoundException,
			IOException {
		URLConnection connection = url.openConnection();
	
		int fileSize = connection.getContentLength();
	
		if (fileSize < 0) {
			throw new FileNotFoundException(url.getFile());
		}
	
		byte[] bytes = new byte[fileSize];
	
		InputStream strean = url.openStream();
		BufferedInputStream buffer = new BufferedInputStream(strean);
	
		buffer.read(bytes);
		buffer.close();
	
		return bytes;
	}
	
	private static byte[] loadFile(final File file) throws FileNotFoundException,
			IOException {
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

}
