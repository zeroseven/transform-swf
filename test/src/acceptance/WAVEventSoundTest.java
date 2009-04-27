package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class WAVEventSoundTest extends EventSoundTest {
	@Test
	public void playWAV() throws IOException, DataFormatException {
		final File sourceDir = new File("test/data/wav/reference");
		final File destDir = new File("test/results/WAVEventSoundTest");

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".wav");
			}
		};

		playSounds(sourceDir, sourceDir.list(filter), destDir);
	}
}
