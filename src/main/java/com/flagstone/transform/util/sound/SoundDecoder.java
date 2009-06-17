package com.flagstone.transform.util.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.sound.DefineSound;

/**
 * SoundDecoder is an interface that classes used to decode different sound
 * formats should implement in order to be registered with the Sound class.
 *
 * @see SoundFactory
 * @see WAVDecoder
 * @see MP3Decoder
 */
public interface SoundDecoder {
    /** TODO(method). */
    void read(File file) throws IOException, DataFormatException;
    /** TODO(method). */
    void read(URL url) throws IOException, DataFormatException;
    /** TODO(method). */
    void read(InputStream stream, int size) throws IOException, DataFormatException;
    /** TODO(method). */
    DefineSound defineSound(int identifier);
    /** TODO(method). */
    List<MovieTag> streamSound(int frameRate);
}
