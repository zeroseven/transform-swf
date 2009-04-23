package debug;

import java.io.File;
import java.io.IOException;

import java.util.zip.DataFormatException;

import java.util.Iterator;
import java.util.List;

import com.flagstone.transform.Background;
import com.flagstone.transform.Bounds;
import com.flagstone.transform.ColorTable;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.WebPalette;
import com.flagstone.transform.coder.MovieTag;

public final class PlayStreamingSound
{
    public static void main(String[] args)
    {
        File sourceFile = new File(args[0]);
        File destFile = new File(args[1]);
        
    	try
    	{
    		if (!destFile.getParentFile().exists()) {
    			destFile.getParentFile().mkdirs();
    		}

            float framesPerSecond = 12.0f;

    		Movie movie = new Movie();
    		List<MovieTag>sound = null; //TODO SoundFactory.streamSound((int)framesPerSecond, sourceFile);

            movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
            movie.setFrameRate(framesPerSecond);

            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
            movie.add(sound.remove(0));

            for (Iterator<MovieTag>i=sound.iterator(); i.hasNext();)
            {
                movie.add(i.next());
                movie.add(ShowFrame.getInstance());
            }

            movie.encodeToFile(destFile.getPath());
    	}
        catch (DataFormatException e)
        {
            System.err.println(sourceFile+": "+e.toString());
        }
        catch (IOException e)
        {
            System.err.println(sourceFile+": "+e.toString());
        }
    }
}
