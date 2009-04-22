package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Bounds;
import com.flagstone.transform.ColorTable;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.WebPalette;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundInfo;
import com.flagstone.transform.sound.StartSound;

public final class PlayEventSoundTest
{
    @Test
    public void playWAV() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/wav/reference");
        File destDir = new File("test/results/PlayEventSoundTest/wav");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".wav");
            }
        };
        
        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }
    
    @Test
    public void playMP3() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/mp3/reference");
        File destDir = new File("test/results/PlayEventSoundTest/mp3");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".mp3");
            }
        };
        
        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }

    private void playSounds(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
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
        	//TODO sound = SoundFactory.defineSound(1, sourceFile);
        	//TODO playSound(sound, destFile);
        }
    }
    
    private void playSound(DefineSound sound, File file) throws IOException, DataFormatException
    {
        float framesPerSecond = 12.0f;

        Movie movie = new Movie();

        movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
        movie.setFrameRate(framesPerSecond);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

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
