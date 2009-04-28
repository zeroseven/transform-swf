package com.flagstone.transform.util.sound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.MovieTag;

/**
 * SoundDecoder is an interface that classes used to decode different sound
 * formats should implement in order to be registered with the Sound class.
 * 
 * @see SoundFactory
 * @see WAVDecoder
 * @see MP3Decoder
 */
public interface SoundDecoder {
    void read(String path) throws FileNotFoundException, IOException,
            DataFormatException;

    void read(File file) throws FileNotFoundException, IOException,
            DataFormatException;

    void read(URL url) throws FileNotFoundException, IOException,
            DataFormatException;

    MovieTag defineSound(int identifier);

    List<MovieTag> streamSound(int frameRate);
}
