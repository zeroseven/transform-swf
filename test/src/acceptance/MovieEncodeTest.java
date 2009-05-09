package acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tools.MovieWriter;

import com.flagstone.transform.Movie;

@RunWith(Parameterized.class)
public final class MovieEncodeTest {
    
    private static File destDir;

    @Parameters
    public static Collection<Object[]>  files() {

        final File srcDir;

        if (System.getProperty("test.suite") == null) {
            srcDir = new File("test/data/swf/reference");
        } else {
            srcDir = new File(System.getProperty("test.suites"));
        }

        destDir = new File("test/results/acceptance", "MovieEncodeTest");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".swf");
            }
        };
        
        String[] files = srcDir.list(filter);
        Object[][] collection = new Object[files.length][2];

        for (int i=0; i<files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
            collection[i][1] = new File(destDir, files[i]);
        }
        return Arrays.asList(collection);
    }

    private File sourceFile;
    private File destFile;

    public MovieEncodeTest(File src, File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void encode() {
       
        try {
            final Movie sourceMovie = new Movie();
            sourceMovie.decodeFromFile(sourceFile);
            sourceMovie.encodeToFile(destFile);

            final Movie destMovie = new Movie();
            destMovie.decodeFromFile(destFile);

            final StringWriter sourceWriter = new StringWriter();
            
            final MovieWriter writer = new MovieWriter();
            writer.write(sourceMovie, sourceWriter);
            
            final StringWriter destWriter = new StringWriter();
            writer.write(destMovie, destWriter);

            assertEquals(sourceWriter.toString(), destWriter.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
