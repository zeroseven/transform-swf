package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

import com.flagstone.transform.Video;

public final class VideoCopyTest {
	private static File srcDir;
	private static File destDir;
	private static FilenameFilter filter;

	@BeforeClass
	public static void setUp() {
		if (System.getProperty("test.suite") == null) {
			srcDir = new File("test/data/flv/reference");
		} else {
			srcDir = new File(System.getProperty("test.suites"));
		}

		filter = new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".flv");
			}
		};

		destDir = new File("test/results", "VideoCopyTest");

		if (!destDir.exists() && !destDir.mkdirs()) {
			fail();
		}
	}

	@Test
	public void encode() throws DataFormatException, IOException {
		File sourceFile = null;
		File destFile = null;

		final Video video = new Video();
		Video copy;

		final String[] files = srcDir.list(filter);

		for (String file : files) {
			sourceFile = new File(srcDir, file);
			destFile = new File(destDir, file);
			video.decodeFromFile(sourceFile);
			copy = video.copy();
			copy.encodeToFile(destFile);
		}
	}
}
