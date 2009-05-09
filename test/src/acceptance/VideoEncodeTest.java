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
import tools.VideoWriter;

import com.flagstone.transform.Movie;
import com.flagstone.transform.Video;

@RunWith(Parameterized.class)
public final class VideoEncodeTest {
    
    private static File destDir;

    @Parameters
    public static Collection<Object[]>  files() {

        final File srcDir;

        if (System.getProperty("test.suite") == null) {
            srcDir = new File("test/data/flv/reference");
        } else {
            srcDir = new File(System.getProperty("test.suites"));
        }

        destDir = new File("test/results/acceptance", "VideoEncodeTest");

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".flv");
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

    public VideoEncodeTest(File src, File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void encode() {
       
        try {
            final Video source = new Video();
            source.decodeFromFile(sourceFile);
            source.encodeToFile(destFile);

            final Video dest = new Video();
            dest.decodeFromFile(destFile);

            final StringWriter sourceWriter = new StringWriter();
            
            final VideoWriter writer = new VideoWriter();
            writer.write(source, sourceWriter);
            
            final StringWriter destWriter = new StringWriter();
            writer.write(dest, destWriter);

            assertEquals(sourceWriter.toString(), destWriter.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
