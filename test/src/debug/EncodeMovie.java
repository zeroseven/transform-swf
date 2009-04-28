package debug;

import java.io.File;

import com.flagstone.transform.Movie;

public final class EncodeMovie {
    public static void main(final String[] args) {
        final File srcFile = new File(args[0]);
        final File destFile = new File(args[1]);
        Movie movie;

        try {
            movie = new Movie();
            movie.decodeFromFile(srcFile);
            movie.encodeToFile(destFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private EncodeMovie() {
        // Private.
    }
}