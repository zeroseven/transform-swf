package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Test;

public final class MP3StreamingSoundTest extends StreamingSoundTest {
    @Test
    public void playMP3() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/mp3/reference");
        final File destDir = new File("test/results/MP3StreamingSoundTest");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".mp3");
            }
        };

        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }
}
