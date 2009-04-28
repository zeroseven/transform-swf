package acceptance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.util.image.ImageFactory;

public class ImageTest {
    protected void showFiles(final File sourceDir, final String[] files,
            final File destDir) throws IOException, DataFormatException {
        File sourceFile;
        File destFile;
        ImageTag image;

        if (!destDir.exists() && !destDir.mkdirs()) {
            throw new FileNotFoundException();
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

    protected void showImage(final ImageTag image, final File file)
            throws IOException, DataFormatException {
        final Movie movie = new Movie();

        final int xOrigin = (image).getWidth() / 2;
        final int yOrigin = (image).getHeight() / 2;

        final DefineShape3 shape = ImageFactory.defineEnclosingShape(movie
                .identifier(), image, -xOrigin, -yOrigin, null);

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
