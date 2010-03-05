package acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tools.VideoWriter;

import com.flagstone.transform.video.Video;

@RunWith(Parameterized.class)
public final class VideoCopyTest {
    
    @Parameters
    public static Collection<Object[]>  files() {

        final File srcDir;

        if (System.getProperty("test.suite") == null) {
            srcDir = new File("test/data/flv/reference");
        } else {
            srcDir = new File(System.getProperty("test.suites"));
        }

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".flv");
            }
        };
        
        String[] files = srcDir.list(filter);
        Object[][] collection = new Object[files.length][1];

        for (int i=0; i<files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
        }
        return Arrays.asList(collection);
    }

    private File file;

    public VideoCopyTest(File file) {
        this.file = file;
    }

    @Test
    public void copy() {
       
        try {
            final Video sourceVideo = new Video();
            sourceVideo.decodeFromFile(file);
 
            final Video destVideo = sourceVideo.copy();
 
            final StringWriter sourceWriter = new StringWriter();
            
            final VideoWriter writer = new VideoWriter();
            writer.write(sourceVideo, sourceWriter);
            
            final StringWriter destWriter = new StringWriter();
            writer.write(destVideo, destWriter);

            assertEquals(sourceWriter.toString(), destWriter.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(file.getPath());
        }
    }
}
