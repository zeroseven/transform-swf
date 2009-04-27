package acceptance;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class PNGImageTest extends ImageTest {
	@Test
	public void showPNG() throws IOException, DataFormatException {
		final File sourceDir = new File("test/data/png/reference");
		final File destDir = new File("test/results/PNGImageTest");

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".png");
			}
		};

		showFiles(sourceDir, sourceDir.list(filter), destDir);
	}
}
