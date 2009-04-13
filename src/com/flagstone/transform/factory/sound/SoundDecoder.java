package com.flagstone.transform.factory.sound;

import java.util.zip.DataFormatException;

import com.flagstone.transform.movie.Copyable;
import com.flagstone.transform.video.SoundFormat;

/**
 * SoundDecoder is an interface that classes used to decode different sound
 * formats should implement in order to be registered with the Sound class.
 * 
 * @see SoundFactory
 * @see WAVDecoder
 * @see MP3Decoder
 */
public abstract class SoundDecoder implements Copyable<SoundDecoder>
{
    protected SoundFormat format;
    protected int numberOfChannels;
    protected int samplesPerChannel;
    protected int sampleRate;
    protected int sampleSize;
    protected byte[] sound = null;

    /**
	 * Creates and returns a copy of this object.
	 */
    public abstract SoundDecoder copy();
    
	/**
	 * Returns one of the sound formats supported in Flash. Either NATIVE_PCM, PCM
	 * or ADPCM (all Flash 1), MP3 (Flash 4+) or NELLYMOSER (Flash 6+).
	 */
    public SoundFormat getFormat()
    {
        return format;
    }
	
	/**
	 * Returns the number of channels in the sound, either 1, mono or 2, stereo.
	 */
    public int getNumberOfChannels()
    {
        return numberOfChannels;
    }

    /**
	 * Returns the number of samples in each channel.
	 */
    public int getSamplesPerChannel()
    {
        return samplesPerChannel;
    }

	/**
	 * Returns the rate at which the sound will be played, in Hz: 5512, 11025,
	 * 22050 or 44100.
	 */
    public int getSampleRate()
    {
        return sampleRate;
    }
 
	/**
	 * Returns the size of an uncompressed sample in bytes.
	 */
    public int getSampleSize()
    {
        return sampleSize;
    }
    
	/**
	 * Returns the sound data.
	 */
    public byte[] getSound()
    {
        byte[] bytes = new byte[sound.length];
        
        System.arraycopy(sound, 0, bytes, 0, sound.length);

        return bytes;
    }
    
	/**
	 * Decode a sound.
	 * 
	 * @param bytes the encoded sound data.
	 * 
	 * @throws DataFormatException if the sound cannot be decoded by the decoder
	 * implementing this interface.
	 */
	protected abstract void decode(byte[] bytes) throws DataFormatException;
}
