package acceptance;

import static org.junit.Assert.fail;

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

public class EventSoundTest
{
    protected void playSounds(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
    {
        File sourceFile;
        File destFile;  
        DefineSound sound;
        
        if (!destDir.exists() && !destDir.mkdirs()) {
        	fail();
        }
        
        for (String file : files)
        {
        	sourceFile = new File(sourceDir, file);
        	destFile = new File(destDir, file.substring(0, file.lastIndexOf('.')) + ".swf");
            sound = SoundFactory.defineSound(1, sourceFile);
        	playSound(sound, destFile);
        }
    }
    
    protected void playSound(DefineSound sound, File file) throws IOException, DataFormatException
    {
        float framesPerSecond = 12.0f;

        Movie movie = new Movie();

        movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
        movie.setFrameRate(framesPerSecond);
        movie.add(new Background(ColorTable.lightblue()));

        float duration = ((float) sound.getSampleCount() / (float) sound.getRate());
        int numberOfFrames = (int) (duration * framesPerSecond);

        movie.add(sound);
        movie.add(new StartSound(new SoundInfo(sound.getIdentifier(), SoundInfo.Mode.START, 0, null)));

        for (int j=0; j<numberOfFrames; j++) {
            movie.add(ShowFrame.getInstance());
        }

        movie.encodeToFile(file.getPath());
    }
}
