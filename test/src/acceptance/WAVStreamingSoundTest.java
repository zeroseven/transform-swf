package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.DataFormatException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.util.sound.SoundFactory;

@RunWith(Parameterized.class)
public final class WAVStreamingSoundTest {
    
    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir = new File("test/data/wav/reference");
        final File destDir = new File("test/results/acceptance/WAVStreamingSoundTest");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".wav");
            }
        };
        
        String[] files = srcDir.list(filter);
        Object[][] collection = new Object[files.length][2];

        for (int i=0; i<files.length; i++) {
            collection[i][0] = new File(srcDir, files[i]);
            collection[i][1] = new File(destDir, 
                    files[i].substring(0, files[i].lastIndexOf('.')) + ".swf");
        }
        return Arrays.asList(collection);
    }

    private File sourceFile;
    private File destFile;

    public WAVStreamingSoundTest(File src, File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void playSound() throws IOException, DataFormatException {

        try {
            final float framesPerSecond = 12.0f;
            final Movie movie = new Movie();
            
            final SoundFactory factory = new SoundFactory();
            factory.read(sourceFile);
            final List<MovieTag> stream = factory.streamSound((int)framesPerSecond);

            movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
            movie.setFrameRate(framesPerSecond);
            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

            movie.add(stream.remove(0));

            for (final MovieTag movieTag : stream) {
                movie.add(movieTag);
                movie.add(ShowFrame.getInstance());
            }

            movie.encodeToFile(destFile);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
