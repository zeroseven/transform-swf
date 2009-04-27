package metrics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import com.flagstone.transform.Movie;

public final class MovieMemoryTest {
	public static void main(final String[] args) {
		File source = new File(args[0]);
		final File destFile = new File(args[1]);

		File sourceDir;
		String[] files;

		if (source.isDirectory()) {
			final FilenameFilter filter = new FilenameFilter() {
				public boolean accept(final File directory, final String name) {
					return name.endsWith(".swf");
				}
			};
			sourceDir = source;
			files = source.list(filter);
		} else {
			sourceDir = source.getParentFile();
			files = new String[] { source.getName() };
		}

		try {
			long before = 0;
			long after = 0;

			final PrintWriter writer = new PrintWriter(destFile);
			writer.append("file").append(',').append("memory").append('\n');

			Movie movie = null;
			byte[] bytes = null;
			ByteArrayInputStream stream;

			for (String file : files) {
				source = new File(sourceDir, file);
				bytes = loadFile(source);
				stream = new ByteArrayInputStream(bytes);

				before = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
				after = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
				// movie = null;
				/*
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 */
				before = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
				movie = new Movie();
				movie.decodeFromStream(stream);
				/*
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 * System.gc(); System.gc(); System.gc(); System.gc();
				 */
				after = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
				writer.append(file).append(',').print(after - before);
				writer.append('\n');
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] loadFile(final File file)
			throws FileNotFoundException, IOException {
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
	
	private MovieMemoryTest() {
		//Private
	}
}
