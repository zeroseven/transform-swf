package com.flagstone.transform.util.text;

import static org.junit.Assert.fail;

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
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.util.font.Font;
import com.flagstone.transform.util.shape.Canvas;

public final class DefineTextTest {
    private static File destDir;
    private static Font font;

    @BeforeClass
    public static void initialize() {
        destDir = new File("test/results/DefineTextTest");

        if (!destDir.mkdirs()) {
            fail();
        }

        font = new Font();
        // TODO font.decode(new java.awt.Font("Arial", java.awt.Font.PLAIN,
        // 12));
    }

    @Test
    public void defineText() throws IOException, DataFormatException {
        final File destFile = new File(destDir, "defineText.swf");

        final Set<Character> set = new LinkedHashSet<Character>();
        final String alphabet = "The quick brown, fox jumped over the lazy dog.";

        for (int i = 0; i < alphabet.length(); i++) {
            set.add(alphabet.charAt(i));
        }

        final int fontSize = 280;
        final int margin = fontSize;
        int layer = 1;
        final int x = margin;
        final int y = margin;

        final Movie movie = new Movie();
        final Canvas path = new Canvas(false);

        final int fontId = movie.identifier();
        final DefineFont2 definition = font.defineFont(fontId, set);

        movie.setFrameRate(1.0f);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(definition);

        final DefineText2 text = null; // TODO
        // TextTable.defineText(movie.newIdentifier(),
        // alphabet, definition, fontSize,
        // ColorTable.black());

        final int textWidth = text.getBounds().getWidth();
        final int textHeight = text.getBounds().getHeight();
        final int shapeId = movie.identifier();

        path.clear();
        path.setLineStyle(new LineStyle(1, WebPalette.DARK_BLUE.color()));
        path.rect(text.getBounds().getMinX(), text.getBounds().getMinY(),
                textWidth, textHeight);

        movie.add(path.defineShape(shapeId));
        movie.add(Place2.show(shapeId, layer++, x + textWidth / 2, y
                + textHeight / 2));

        movie.add(text);
        movie.add(Place2.show(text.getIdentifier(), layer++, x, y));
        movie.add(ShowFrame.getInstance());

        movie.setFrameSize(new Bounds(0, 0, textWidth + 2 * margin, textHeight
                + 2 * margin));
        movie.encodeToFile(destFile);
    }

    @Test
    public void bounds() throws IOException, DataFormatException {
        final File destFile = new File(destDir, "bounds.swf");

        final Set<Character> set = new LinkedHashSet<Character>();
        final String alphabet = "abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < alphabet.length(); i++) {
            set.add(alphabet.charAt(i));
        }

        final int fontSize = 280;
        final int lineSpacing = fontSize;
        final int margin = fontSize;
        final int charsPerLine = 32;
        int layer = 1;

        final Movie movie = new Movie();

        int maxWidth = 0;
        int x = margin;
        int y = margin;

        final int fontId = movie.identifier();
        final DefineFont2 definition = font.defineFont(fontId, set);
        final Canvas path = new Canvas(false);

        movie.setFrameSize(new Bounds(0, 0, 0, 0));
        movie.setFrameRate(1.0f);
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
        movie.add(definition);

        for (int i = 0; i < alphabet.length(); i++) {
            final DefineText2 text = null; // TODO
            // TextFactory.defineText(movie.newIdentifier(),
            // alphabet.substring(i,i+1),
            // definition, fontSize,
            // ColorTable.black());

            final int textWidth = text.getBounds().getWidth();
            final int textHeight = text.getBounds().getHeight();
            final int advance = 0; // TODO
            // TextFactory.boundsForText(alphabet.substring(i,i+1),
            // definition, fontSize).getWidth() + 40;

            final int shapeId = movie.identifier();

            path.clear();
            path.setLineStyle(new LineStyle(1, WebPalette.DARK_BLUE.color()));
            path.rect(text.getBounds().getMinX(), text.getBounds().getMinY(),
                    textWidth, textHeight);

            movie.add(path.defineShape(shapeId));
            movie.add(Place2.show(shapeId, layer++, x + textWidth / 2, y
                    + textHeight / 2));

            movie.add(text);
            movie.add(Place2.show(text.getIdentifier(), layer++, x, y));

            if (i % charsPerLine == charsPerLine - 1) {
                maxWidth = x + advance + margin > maxWidth ? x + advance
                        + margin : maxWidth;

                x = margin;
                y += lineSpacing;
            } else {
                x += advance;
            }
        }
        movie.setFrameSize(new Bounds(0, 0, maxWidth, y + margin));

        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(destFile);
    }
}
