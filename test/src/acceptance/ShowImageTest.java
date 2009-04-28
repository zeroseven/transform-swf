package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Ignore;
import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.util.image.ImageFactory;

public final class ShowImageTest {
    @Test
    public void showBMP() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/bmp/reference");
        final File destDir = new File("test/results/ShowImageTest/bmp");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".bmp");
            }
        };

        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }

    @Test
    public void showPNG() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/png/reference");
        final File destDir = new File("test/results/ShowImageTest/png");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".png");
            }
        };

        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }

    @Test
    @Ignore
    public void showJPG() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/jpg/reference");
        final File destDir = new File("test/results/ShowImageTest/jpg");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".jpg");
            }
        };

        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }

    private void showFiles(final File sourceDir, final String[] files,
            final File destDir) throws IOException, DataFormatException {
        File sourceFile;
        File destFile;
        ImageTag image;

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        for (final String file : files) {
            sourceFile = new File(sourceDir, file);
            destFile = new File(destDir, file.substring(0, file
                    .lastIndexOf('.'))
                    + ".swf");
            image = ImageFactory.defineImage(10, sourceFile);
            showImage(image, destFile);
        }
    }

    private void showImage(final ImageTag image, final File file)
            throws IOException, DataFormatException {
        final Movie movie = new Movie();

        final int xOrigin = (image).getWidth() / 2;
        final int yOrigin = (image).getHeight() / 2;

        final LineStyle borderStyle = new LineStyle(20, WebPalette.BLACK
                .color());

        final DefineShape3 shape = ImageFactory.defineEnclosingShape(movie
                .identifier(), image, -xOrigin, -yOrigin, borderStyle);

        movie.setFrameRate(1.0f);
        movie.setFrameSize(shape.getBounds());
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(image);
        movie.add(shape);
        movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(file);
    }
}
