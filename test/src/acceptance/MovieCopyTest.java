package acceptance;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

import com.flagstone.transform.Movie;

public final class MovieCopyTest
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

   		destDir = new File("test/results", "MovieCopyTest");		

		if (destDir.exists() == false && destDir.mkdirs() == false) {
			fail();
		}
	}
	
    @Test
    public void encode()
    {
		File sourceFile = null;		
		File destFile = null;
		
        Movie movie = new Movie();
        Movie copy;
 		
        try
        {
			String[] files = srcDir.list(filter);
				
			for (String file : files) {
				sourceFile = new File(srcDir, file); 
				destFile = new File(destDir, file); 
			    movie.decodeFromFile(sourceFile);
			    copy = movie.copy();
			    copy.encodeToFile(destFile);
	        }
        }
		catch (Throwable t)
		{
			t.printStackTrace();
			
			fail(sourceFile.getPath());
		}
    }
}
