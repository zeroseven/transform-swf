package acceptance;

import static org.junit.Assert.fail;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.text.DefineTextField;
import com.flagstone.transform.util.font.Font;

public final class ShowAWTFontTest {
    private static Set<Character> set;
    private static String alphabet;

    private static int width = 6000;
    private static int height = 1000;
    private static int margin = 400;
    private static int fontSize = 360;

    private static int screenWidth = width + margin;
    private static int screenHeight = height + margin;

    @BeforeClass
    public static void setUp() {
        set = new LinkedHashSet<Character>();
        alphabet = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < alphabet.length(); i++) {
            set.add(alphabet.charAt(i));
        }
    }

    @Test
    public void showAWT() throws IOException, DataFormatException {
        final File destDir = new File("test/results/ShowAWTFontTest");
        File destFile;

        if (!destDir.exists() && !destDir.mkdirs()) {
            fail();
        }

        Font font;
        DefineFont2 definition;

        final java.awt.Font[] fonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getAllFonts();

        for (final java.awt.Font font2 : fonts) {
            try {
                font = new Font();
                // TODOfont.decode(fonts[i]);
                destFile = new File(destDir, font2.getFontName() + ".swf");
                definition = font.defineFont(1, set);
                showFont(definition, destFile);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showFont(final DefineFont2 font, final File file)
            throws IOException, DataFormatException {
        final Movie movie = new Movie();

        final DefineTextField text = new DefineTextField(
                font.getIdentifier() + 1);
        text.setBounds(new Bounds(0, 0, width, height));
        text.setVariableName("var");
        text.setInitialText(alphabet);

        text.setUseFontGlyphs(true);
        text.setFontIdentifier(font.getIdentifier());
        text.setFontHeight(fontSize);
        text.setColor(WebPalette.BLACK.color());

        movie.setFrameSize(new Bounds(0, 0, screenWidth, screenHeight));
        movie.setFrameRate(1.0f);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(font);
        movie.add(text);
        movie.add(Place2.show(text.getIdentifier(), 1, margin, margin));
        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(file);
    }
}
