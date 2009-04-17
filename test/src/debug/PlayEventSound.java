package debug;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import java.util.zip.DataFormatException;

import com.flagstone.transform.factory.sound.SoundFactory;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.ColorTable;
import com.flagstone.transform.movie.sound.DefineSound;
import com.flagstone.transform.movie.sound.SoundLevel;
import com.flagstone.transform.movie.sound.SoundInfo;
import com.flagstone.transform.movie.sound.StartSound;

public final class PlayEventSound
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
    		
    		Movie movie = new Movie();
    		DefineSound sound = SoundFactory.defineSound(movie.newIdentifier(), sourceFile);
    		
            float framesPerSecond = 12.0f;

            movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
            movie.setFrameRate(framesPerSecond);

            movie.add(new Background(ColorTable.lightblue()));

            int soundId = movie.newIdentifier();

            /*
             * Calculate the time it takes to play the sound and the number of frames this
             * represents.
             */
            float duration = ((float) sound.getSampleCount()) / ((float) sound.getRate());
            int numberOfFrames = (int) (duration * framesPerSecond);

            /*
             * Add the sound definition and the FSStartSound object which is used to start
             * the sound playing.
             */

            movie.add(sound);
            movie.add(new StartSound(new SoundInfo(soundId, SoundInfo.Mode.START, 1, null)));

            /*
             * Add frames to give the sound time to play.
             */
            for (int j=0; j<numberOfFrames; j++) {
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
