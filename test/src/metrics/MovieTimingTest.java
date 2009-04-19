package metrics;

import static org.junit.Assert.fail;

import java.util.zip.DataFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.movie.Movie;

public final class MovieTimingTest
{
    private static File dataDir;
	private static FilenameFilter filter;
    private static PrintWriter writer;
    private static File resultDir;
    private static File resultFile;
    private static int iterations = 10;   	
        
	private static String[] suites = new String[] {
		"swf-v4-classes",
		"swf-v5-classes",
		"swf-v6-classes",
		"swf-v7-classes",
	};
	
    @BeforeClass
    public static void initialize() throws IOException
    {
		dataDir = new File("test/data");
		resultDir = new File("test/results/MovieTimingTest");
		
		if (resultDir.mkdirs() == false) {
			fail();
		}

    	writer = new PrintWriter(new File(resultDir, "memory.txt"));    	
		writer.append("file,decode,lazy-decode,encode,lazy-encode,clone");

    	filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".swf");
            }
        };
    }
    
    @AfterClass
    public static void report() throws IOException
    {
    	writer.close();    	
    }

    @Test
    public void decode() throws IOException, DataFormatException
    {
		File sourceDir;
		File sourceFile;
       	String[] files;
		
		for (String suite : suites)  
		{
			sourceDir = new File(dataDir, suite);
			files = sourceDir.list(filter);
				
			for (String file : files)  
			{
	        	sourceFile = new File(sourceDir, file);	
	        	
	            writer.append(file);
	        		
	            System.gc();
	            
	            decode(sourceFile);
	            lazyDecode(sourceFile);
	            encode(sourceFile);
	            lazyEncode(sourceFile);
	            copy(sourceFile);	
	        }
		}
    }

    private void decode(File source) throws FileNotFoundException, IOException, DataFormatException
    {
		byte[] bytes = loadFile(source);

        double before = System.currentTimeMillis();

        for (int i=0; i<iterations; i++) {
            new Movie().decodeFromData(bytes);
        }
        
        double duration = (System.currentTimeMillis()-before)/iterations;

		writer.append(',');
		writer.print(duration);
    }
    
	private byte[] loadFile(final File file) throws FileNotFoundException,
			IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; //TODO(code) fix

		try {
			stream = new FileInputStream(file);
			int bytesRead = stream.read(data);

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

	 
    private void lazyDecode(File source) throws FileNotFoundException, IOException, DataFormatException
    {
		Movie movie = new Movie();
        movie.setDecodeActions(false);
        movie.setDecodeShapes(false);
        movie.setDecodeGlyphs(false);

        byte[] bytes = loadFile(source);

        double  before = System.currentTimeMillis();

        for (int i=0; i<iterations; i++) {
            new Movie().decodeFromData(bytes);
        }
        
        double duration = (System.currentTimeMillis()-before)/iterations;

		writer.append(',');
		writer.print(duration);
    }
	 
    private void encode(File source) throws FileNotFoundException, IOException, DataFormatException
    {
		Movie movie = new Movie();
        movie.decodeFromFile(source);

        System.gc();

        double before = System.currentTimeMillis();

        for (int i=0; i<iterations; i++) {
            movie.encode();
        }
        
        double duration = (System.currentTimeMillis()-before)/iterations;

		writer.append(',');
		writer.print(duration);
    }

    private void lazyEncode(File source) throws FileNotFoundException, IOException, DataFormatException
    {
		Movie movie = new Movie();
        movie.setDecodeActions(false);
        movie.setDecodeShapes(false);
        movie.setDecodeGlyphs(false);
        movie.decodeFromFile(source);

        System.gc();

        double before = System.currentTimeMillis();

        for (int i=0; i<iterations; i++) {
            movie.encode();
        }
        
        double duration = (System.currentTimeMillis()-before)/iterations;

		writer.append(',');
		writer.print(duration);
		writer.append('\n');
    }
     
    public static void copy(File source) throws FileNotFoundException, IOException, DataFormatException
    {
		Movie movie = new Movie();
        movie.decodeFromFile(source);

        System.gc();

        double before = System.currentTimeMillis();

	    for (int i=0; i<iterations; i++) {
	        movie.copy();
	    }
         
        double duration = (System.currentTimeMillis()-before)/iterations;

		writer.append(',');
		writer.print(duration);
    }
}
