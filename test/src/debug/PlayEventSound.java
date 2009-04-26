package debug;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import java.util.zip.DataFormatException;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.ColorTable;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundInfo;
import com.flagstone.transform.sound.StartSound;

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
    		DefineSound sound = null; //TODO SoundFactory.defineSound(movie.newIdentifier(), sourceFile);
    		
            float framesPerSecond = 12.0f;

            movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
            movie.setFrameRate(framesPerSecond);

            movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

            int soundId = movie.identifier();

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

            movie.encodeToFile(destFile);
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
