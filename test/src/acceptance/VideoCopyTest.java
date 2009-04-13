package acceptance;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

import com.flagstone.transform.video.Video;

public final class VideoCopyTest
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
			srcDir = new File("test/data/flv/reference");
		}
		
        filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".flv");
            }
        };

   		destDir = new File("test/results", "VideoCopyTest");		

		if (destDir.exists() == false && destDir.mkdirs() == false) {
			fail();
		}
	}
	
    @Test
    public void encode()
    {
		File sourceFile = null;		
		File destFile = null;
		
        Video video = new Video();
        Video copy;
 		
        try
        {
			String[] files = srcDir.list(filter);
				
			for (String file : files) {
				sourceFile = new File(srcDir, file); 
				destFile = new File(destDir, file); 
			    video.decodeFromFile(sourceFile);
			    copy = video.copy();
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
