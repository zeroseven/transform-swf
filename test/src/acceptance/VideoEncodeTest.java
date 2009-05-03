package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.Video;

public final class VideoEncodeTest {
    private static File srcDir;
    private static File destDir;
    private static FilenameFilter filter;

    @BeforeClass
    public static void setUp() {
        if (System.getProperty("test.suite") == null) {
            srcDir = new File("test/data/flv/reference");
        } else {
            srcDir = new File(System.getProperty("test.suites"));
        }

        filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".flv");
            }
        };

        destDir = new File("test/results", "VideoEncodeTest");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }
    }

    @Test
    public void encode() throws DataFormatException, IOException {
        File sourceFile = null;
        File destFile = null;
        final Video video = new Video();

        final String[] files = srcDir.list(filter);

        for (final String file : files) {
            sourceFile = new File(srcDir, file);
            destFile = new File(destDir, file);
            video.decodeFromFile(sourceFile);
            video.encodeToFile(destFile);
        }
    }
}
