package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class WAVEventSoundTest extends EventSoundTest
{
    @Test
    public void playWAV() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/wav/reference");
        File destDir = new File("test/results/WAVEventSoundTest");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".wav");
            }
        };
        
        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }
}
