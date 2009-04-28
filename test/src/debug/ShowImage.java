package debug;

import java.io.File;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.util.image.ImageFactory;

public final class ShowImage {
    public static void main(final String[] args) {

        final String sourceFile = args[0];
        final String destFile = args[1];
        Movie movie;

        try {
            movie = new Movie();
            final ImageTag image = ImageFactory.defineImage(movie.identifier(),
                    sourceFile);

            final int imageWidth = image.getWidth();
            final int imageHeight = image.getHeight();
            final int screenWidth = imageWidth * 20;
            final int screenHeight = imageHeight * 20;
            final int shapeId = movie.identifier();

            movie.setFrameRate(1.0f);
            movie.setSignature(Movie.Signature.FWS);
            movie.setFrameSize(new Bounds(-screenWidth / 2, -screenHeight / 2,
                    screenWidth / 2, screenHeight / 2));

            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(image);
            movie.add(ImageFactory.defineEnclosingShape(shapeId, image,
                    imageWidth / 2, imageHeight / 2, null));
            movie.add(Place2.show(shapeId, 1, 0, 0));
            movie.add(ShowFrame.getInstance());

            movie.encodeToFile(new File(destFile));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private ShowImage() {
        // Private.
    }
}
