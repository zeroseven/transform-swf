package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Test;

public final class JPGImageTest extends ImageTest {
    @Test
    public void showJPG() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/jpg/reference");
        final File destDir = new File("test/results/JPGImageTest");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".jpg");
            }
        };

        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
}
