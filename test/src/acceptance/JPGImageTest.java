package acceptance;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.zip.DataFormatException;


import org.junit.Test;

public final class JPGImageTest extends ImageTest
{
    @Test
    public void showJPG() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/jpg/reference");
        File destDir = new File("test/results/JPGImageTest");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".jpg");
            }
        };
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
}
