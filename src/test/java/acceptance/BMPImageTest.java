package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.DataFormatException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.util.image.ImageFactory;

@RunWith(Parameterized.class)
public final class BMPImageTest {
    
    @Parameters
    public static Collection<Object[]> files() {

        final File srcDir = new File("test/data/bmp/reference");
        final File destDir = new File("test/results/acceptance/BMPImageTest");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".bmp");
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

    public BMPImageTest(File src, File dst) {
        sourceFile = src;
        destFile = dst;
    }

    @Test
    public void showImage() {

        try {
            final Movie movie = new Movie();
            final ImageFactory factory = new ImageFactory();
            factory.read(sourceFile);
            final ImageTag image = factory.defineImage(movie.identifier());
    
            final int xOrigin = image.getWidth() / 2;
            final int yOrigin = image.getHeight() / 2;
    
            final DefineShape3 shape = factory.defineEnclosingShape(movie
                    .identifier(), 10, -xOrigin, -yOrigin, null);
    
            movie.setFrameRate(1.0f);
            movie.setFrameSize(shape.getBounds());
            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(image);
            movie.add(shape);
            movie.add(Place2.show(shape, 1, 0, 0));
            movie.add(ShowFrame.getInstance());
            movie.encodeToFile(destFile);
        
        } catch (Exception e) {
            e.printStackTrace();
            fail(sourceFile.getPath());
        }
    }
}
