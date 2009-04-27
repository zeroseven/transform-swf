package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;

import org.junit.Test;

import static org.junit.Assert.fail;

public final class PlayStreamingSoundTest {
	private float framesPerSecond = 12.0f;

	@Test
	public void playWAV() throws IOException, DataFormatException {
		final File sourceDir = new File("test/data/wav/reference");
		final File destDir = new File("test/results/PlayStreamingSoundTest/wav");

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".wav");
			}
		};

		playSounds(sourceDir, sourceDir.list(filter), destDir);
	}

	@Test
	public void playMP3() throws IOException, DataFormatException {
		final File sourceDir = new File("test/data/mp3/reference");
		final File destDir = new File("test/results/PlayStreamingSoundTest/mp3");

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".mp3");
			}
		};

		playSounds(sourceDir, sourceDir.list(filter), destDir);
	}

	private void playSounds(final File sourceDir, final String[] files, final File destDir)
			throws IOException, DataFormatException {
		File sourceFile;
		File destFile;
		final List<MovieTag> stream;

		if (!destDir.exists() && !destDir.mkdirs()) {
			fail();
		}

		for (String file : files) {
			sourceFile = new File(sourceDir, file);
			destFile = new File(destDir, file.substring(0, file
					.lastIndexOf('.'))
					+ ".swf");
			// TODO stream = SoundFactory.streamSound((int)framesPerSecond,
			// sourceFile);
			// TODO playSound(stream, destFile);
		}
	}

	private void playSound(final List<MovieTag> stream, final File file)
			throws IOException, DataFormatException {
		final Movie movie = new Movie();

		movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
		movie.setFrameRate(framesPerSecond);
		movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

		movie.add(stream.remove(0));

		for (final Iterator<MovieTag> i = stream.iterator(); i.hasNext();) {
			movie.add(i.next());
			movie.add(ShowFrame.getInstance());
		}

		movie.encodeToFile(file);
	}
}
