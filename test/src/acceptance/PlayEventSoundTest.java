package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.sound.DefineSound;
import com.flagstone.transform.sound.SoundInfo;
import com.flagstone.transform.sound.StartSound;

public final class PlayEventSoundTest {
    @Test
    public void playWAV() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/wav/reference");
        final File destDir = new File("test/results/PlayEventSoundTest/wav");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".wav");
            }
        };

        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }

    @Test
    public void playMP3() throws IOException, DataFormatException {
        final File sourceDir = new File("test/data/mp3/reference");
        final File destDir = new File("test/results/PlayEventSoundTest/mp3");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(final File directory, final String name) {
                return name.endsWith(".mp3");
            }
        };

        playSounds(sourceDir, sourceDir.list(filter), destDir);
    }

    private void playSounds(final File sourceDir, final String[] files,
            final File destDir) throws IOException, DataFormatException {
        File sourceFile;
        File destFile;
        final DefineSound sound;

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        for (final String file : files) {
            sourceFile = new File(sourceDir, file);
            destFile = new File(destDir, file.substring(0, file
                    .lastIndexOf('.'))
                    + ".swf");
            // TODO(code) sound = SoundFactory.defineSound(1, sourceFile);
            // playSound(sound, destFile);
        }
    }

    private void playSound(final DefineSound sound, final File file)
            throws IOException, DataFormatException {
        final float framesPerSecond = 12.0f;

        final Movie movie = new Movie();

        movie.setFrameSize(new Bounds(0, 0, 8000, 4000));
        movie.setFrameRate(framesPerSecond);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

        final float duration = ((float) sound.getSampleCount() / (float) sound
                .getRate());
        final int numberOfFrames = (int) (duration * framesPerSecond);

        movie.add(sound);
        movie.add(new StartSound(new SoundInfo(sound.getIdentifier(),
                SoundInfo.Mode.START, 0, null)));

        for (int j = 0; j < numberOfFrames; j++) {
            movie.add(ShowFrame.getInstance());
        }

        movie.encodeToFile(file);
    }
}
