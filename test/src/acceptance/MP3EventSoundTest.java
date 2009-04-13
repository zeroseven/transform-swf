package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class MP3EventSoundTest extends EventSoundTest
{
    @Test
    public void playMP3() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/mp3/reference");
        File destDir = new File("test/results/MP3EventSoundTest");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".mp3");
            }
        };
        
        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }
}
