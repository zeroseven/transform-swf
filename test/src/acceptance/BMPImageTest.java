package acceptance;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.zip.DataFormatException;

import org.junit.Test;

public final class BMPImageTest extends ImageTest
{
	@Test
    public void showBMP() throws IOException, DataFormatException {
		
        File sourceDir = new File("test/data/bmp/reference");
        File destDir = new File("test/results/BMPImageTest");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".bmp");
            }
        };
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
     }
}
