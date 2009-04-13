package metrics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;

import com.flagstone.transform.movie.Movie;

public final class MovieMemoryTest
{
    public static void main(String[] args)
    {
       	File source = new File(args[0]);
       	File destFile = new File(args[1]);
       	
       	File sourceDir;
       	String[] files;
       	
       	if (source.isDirectory()) 
        {
            FilenameFilter filter = new FilenameFilter()
            {
                public boolean accept(File directory, String name) {
                    return name.endsWith(".swf");
                }
            };
            sourceDir = source;
            files = source.list(filter);	
       	}
       	else 
       	{
       		sourceDir = source.getParentFile();
       		files = new String[] { source.getName() };
       	}
       		
		try
		{
	       	long before = 0;
	       	long after = 0;
	       	
	       	PrintWriter writer = new PrintWriter(destFile);
			writer.append("file").append(',').append("memory").append('\n');
	   
			Movie movie = null;
			byte[] bytes = null;
			
	        for (String file : files)  
	        {
	            source = new File(sourceDir, file);
	            bytes = loadFile(source);
	
	            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	            movie = null;
	
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	            
	            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	            movie = new Movie();
	            movie.decodeFromData(bytes);
	              
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	            System.gc(); System.gc(); System.gc(); System.gc();
	              
	            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	            writer.append(file).append(',').print(after-before);
	            writer.append('\n');
	        }
	        writer.close();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
    }
    
	private static byte[] loadFile(final File file)
			throws FileNotFoundException, IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; // NOPMD

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
}
