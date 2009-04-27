package acceptance;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class BMPImageTest extends ImageTest {
	@Test
	public void showBMP() throws IOException, DataFormatException {

		final File sourceDir = new File("test/data/bmp/reference");
		final File destDir = new File("test/results/BMPImageTest");

		final FilenameFilter filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".bmp");
			}
		};

		showFiles(sourceDir, sourceDir.list(filter), destDir);
	}
}
