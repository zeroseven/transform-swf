package acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;

import tools.MovieWriter;

import com.flagstone.transform.Movie;

public final class MovieEncodeTest {
    private static File srcDir;
    private static File destDir;
    private static FilenameFilter filter;

    @BeforeClass
    public static void setUp() {
        if (System.getProperty("test.suite") == null) {
            srcDir = new File("test/data/swf/reference");
        } else {
            srcDir = new File(System.getProperty("test.suites"));
        }

        filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".swf");
            }
        };

        destDir = new File("test/results", "MovieEncodeTest");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }
    }

    @Test
    public void encode() throws DataFormatException, IOException {
        File sourceFile = null;
        File destFile = null;

        final Movie sourceMovie = new Movie();
        final Movie destMovie = new Movie();
        final MovieWriter writer = new MovieWriter();

        StringWriter sourceWriter = null;
        StringWriter destWriter = null;

        final String[] files = srcDir.list(filter);

        for (final String file : files) {
            sourceFile = new File(srcDir, file);
            destFile = new File(destDir, file);

            sourceMovie.decodeFromFile(sourceFile);
            sourceMovie.encodeToFile(destFile);
            sourceWriter = new StringWriter();
            writer.write(sourceMovie, sourceWriter);

            destMovie.decodeFromFile(destFile);
            destWriter = new StringWriter();
            writer.write(destMovie, destWriter);

            assertEquals(sourceWriter.toString(), destWriter.toString());
        }
    }
}
