package debug;

import java.io.File;
import java.util.List;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;

public final class PlayStreamingSound {
    public static void main(final String[] args) {

        final File sourceFile = new File(args[0]);
        final File destFile = new File(args[1]);

        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            final float framesPerSecond = 12.0f;

            final Movie movie = new Movie();
            final List<MovieTag> sound = null; // TODO
            // SoundFactory.streamSound((int)framesPerSecond,
            // sourceFile);

            movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
            movie.setFrameRate(framesPerSecond);

            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(sound.remove(0));

            for (final MovieTag movieTag : sound) {
                movie.add(movieTag);
                movie.add(ShowFrame.getInstance());
            }

            movie.encodeToFile(destFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private PlayStreamingSound() {
        // Private.
    }
}
