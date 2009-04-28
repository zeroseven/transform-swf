package metrics;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.DataFormatException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.Movie;

public final class MovieTimingTest {

    private static File dataDir;
    private static FilenameFilter filter;
    private static PrintWriter writer;
    private static File resultDir;
    private static int iterations = 10;

    private static String[] suites = new String[] { "swf-v4-classes",
            "swf-v5-classes", "swf-v6-classes", "swf-v7-classes", };

    @BeforeClass
    public static void initialize() throws IOException {
        dataDir = new File("test/data");
        resultDir = new File("test/results/MovieTimingTest");

        if (!resultDir.mkdirs()) {
            fail();
        }

        writer = new PrintWriter(new File(resultDir, "memory.txt"));
        writer.append("file,decode,lazy-decode,encode,lazy-encode,clone");

        filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".swf");
            }
        };
    }

    @AfterClass
    public static void report() throws IOException {
        writer.close();
    }

    @Test
    public void decode() throws IOException, DataFormatException {
        File sourceDir;
        File sourceFile;
        String[] files;

        for (final String suite : suites) {
            sourceDir = new File(dataDir, suite);
            files = sourceDir.list(filter);

            for (final String file : files) {
                sourceFile = new File(sourceDir, file);

                writer.append(file);

                decode(sourceFile);
                encode(sourceFile);
                copy(sourceFile);
            }
        }
    }

    private void decode(final File source) throws FileNotFoundException,
            IOException, DataFormatException {
        final byte[] bytes = loadFile(source);

        final double before = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            new Movie().decodeFromStream(new ByteArrayInputStream(bytes));
        }

        final double duration = (System.currentTimeMillis() - before)
                / iterations;

        writer.append(',');
        writer.print(duration);
    }

    private byte[] loadFile(final File file) throws FileNotFoundException,
            IOException {
        final byte[] data = new byte[(int) file.length()];

        FileInputStream stream = null;

        try {
            stream = new FileInputStream(file);
            final int bytesRead = stream.read(data);

            if (bytesRead != data.length) {
                throw new IOException(file.getAbsolutePath());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return data;
    }

    private void encode(final File source) throws FileNotFoundException,
            IOException, DataFormatException {
        final Movie movie = new Movie();
        movie.decodeFromFile(source);

        final double before = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            movie.encode();
        }

        final double duration = (System.currentTimeMillis() - before)
                / iterations;

        writer.append(',');
        writer.print(duration);
    }

    public static void copy(final File source) throws FileNotFoundException,
            IOException, DataFormatException {
        final Movie movie = new Movie();
        movie.decodeFromFile(source);

        final double before = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            movie.copy();
        }

        final double duration = (System.currentTimeMillis() - before)
                / iterations;

        writer.append(',');
        writer.print(duration);
    }
}
