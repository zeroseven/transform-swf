package com.flagstone.transform.util.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.MovieTag;

/**
 * SoundDecoder is an interface that classes used to decode different sound
 * formats should implement in order to be registered with the Sound class.
 * 
 * @see SoundFactory
 * @see WAVDecoder
 * @see MP3Decoder
 */
public interface SoundDecoder
{
    public void read(String path) throws FileNotFoundException, IOException, DataFormatException;
    public void read(File file) throws FileNotFoundException, IOException, DataFormatException;
    public void read(URL url) throws FileNotFoundException, IOException, DataFormatException;
    
    public MovieTag defineSound(int identifier);
    public List<MovieTag> streamSound(int frameRate);
}
