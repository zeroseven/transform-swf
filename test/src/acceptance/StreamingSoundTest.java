package acceptance;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.ColorTable;
import com.flagstone.transform.datatype.WebPalette;

import static org.junit.Assert.fail;

public class StreamingSoundTest
{
    private float framesPerSecond = 12.0f;

    protected void playSounds(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
    {
        File sourceFile;
        File destFile;  
        List<MovieTag>stream;
        
        if (!destDir.exists() && !destDir.mkdirs()) {
        	fail();
        }
        
        for (String file : files)
        {
        	sourceFile = new File(sourceDir, file);
        	destFile = new File(destDir, file.substring(0, file.lastIndexOf('.')) + ".swf");
        	//TODO stream = SoundFactory.streamSound((int)framesPerSecond, sourceFile);
        	//TODO playSound(stream, destFile);
        }
    }
    
    protected void playSound(List<MovieTag>stream, File file) throws IOException, DataFormatException
    {
        Movie movie = new Movie();

        movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
        movie.setFrameRate(framesPerSecond);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

        movie.add(stream.remove(0));

        for (Iterator<MovieTag>i=stream.iterator(); i.hasNext();)
        {
            movie.add(i.next());
            movie.add(ShowFrame.getInstance());
        }

        movie.encodeToFile(file.getPath());
    }
}
