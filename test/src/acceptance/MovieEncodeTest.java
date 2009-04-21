package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;

import org.junit.BeforeClass;
import org.junit.Test;

import tools.MovieWriter;

import static org.junit.Assert.*;

import com.flagstone.transform.Movie;

public final class MovieEncodeTest
{	
	private static File srcDir;
	private static File destDir;
	private static FilenameFilter filter;
	
	@BeforeClass
	public static void setup()
	{
		if (System.getProperty("test.suite") != null) {
			srcDir = new File(System.getProperty("test.suites"));
		}
		else {
			srcDir = new File("test/data/swf/reference");
		}
		
        filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".swf");
            }
        };

   		destDir = new File("test/results", "MovieEncodeTest");		

		if (destDir.exists() == false && destDir.mkdirs() == false) {
			fail();
		}
	}
	
    @Test
    public void encode()
    {
		File sourceFile = null;		
		File destFile = null;
		
        Movie sourceMovie = new Movie();
        Movie destMovie = new Movie();
       
        MovieWriter writer = new MovieWriter();
        
        StringWriter sourceWriter = null;
        StringWriter destWriter = null;
 		
        try
        {
			String[] files = srcDir.list(filter);
				
			for (String file : files) {
				sourceFile = new File(srcDir, file); 
				destFile = new File(destDir, file); 
				
			    sourceMovie.decodeFromFile(sourceFile);
	            sourceMovie.encodeToFile(destFile);
	            sourceWriter = new StringWriter();
	            writer.write(sourceMovie, sourceWriter);
	            
	            destMovie.decodeFromFile(destFile);
	            destWriter = new StringWriter();
	            writer.write(destMovie, destWriter);
	            
	            assertEquals(sourceWriter.toString(), destWriter.toString());
	        }
        }
		catch (Throwable t)
		{
			t.printStackTrace();
			
			fail(sourceFile.getPath());
		}
    }
}
