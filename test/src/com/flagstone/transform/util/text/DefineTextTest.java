package com.flagstone.transform.util.text;

import java.io.File;
import java.io.IOException;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.zip.DataFormatException;


import com.flagstone.transform.Background;
import com.flagstone.transform.Bounds;
import com.flagstone.transform.ColorTable;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.font.DefineFont2;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.text.DefineText2;
import com.flagstone.transform.util.font.Font;
import com.flagstone.transform.util.shape.Canvas;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public final class DefineTextTest
{
	private static File destDir;
	private static Font font;
	
	@BeforeClass
	public static void initialize()
	{
		destDir = new File("test/results/DefineTextTest");
		
        if (destDir.mkdirs() == false) {
        	fail();
        }	

        font = new Font();
        //TODO font.decode(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
	}
	
	@Test
	public void defineText() throws IOException, DataFormatException 
	{
        File destFile = new File(destDir, "defineText.swf");

    	Set<Character> set = new LinkedHashSet<Character>();
    	String alphabet = "The quick brown, fox jumped over the lazy dog.";
    	
    	for (int i=0; i<alphabet.length(); i++) {
    		set.add(alphabet.charAt(i));
    	}

    	int fontSize = 280;
        int margin = fontSize;
        int layer = 1;
        int x = margin;
        int y = margin;

        Movie movie = new Movie();
        Canvas path = new Canvas(false);

        int fontId = movie.newIdentifier();
        DefineFont2 definition = font.defineFont(fontId, set);

        movie.setFrameRate(1.0f);
        movie.add(new Background(ColorTable.lightblue()));
        movie.add(definition);

        DefineText2 text = null; //TODO TextTable.defineText(movie.newIdentifier(), alphabet, definition, fontSize, ColorTable.black());

        int textWidth = text.getBounds().getWidth();
        int textHeight = text.getBounds().getHeight();
        int shapeId = movie.newIdentifier();

        path.clear();
        path.setLineStyle(new LineStyle(1, ColorTable.darkblue()));
        path.rect(text.getBounds().getMinX(), text.getBounds().getMinY(),  textWidth, textHeight);

        movie.add(path.defineShape(shapeId));
        movie.add(Place2.show(shapeId, layer++, x+textWidth/2, y+textHeight/2));

        movie.add(text);
        movie.add(Place2.show(text.getIdentifier(), layer++, x, y));
        movie.add(ShowFrame.getInstance());

        movie.setFrameSize(new Bounds(0, 0, textWidth+2*margin, textHeight+2*margin));
        movie.encodeToFile(destFile);
	}

	@Test
	public void bounds() throws IOException, DataFormatException 
	{
        File destFile = new File(destDir, "bounds.swf");
    	
    	Set<Character> set = new LinkedHashSet<Character>();
    	String alphabet = "abcdefghijklmnopqrstuvwxyz";
    	
    	for (int i=0; i<alphabet.length(); i++) {
    		set.add(alphabet.charAt(i));
    	}

        int fontSize = 280;
        int lineSpacing = fontSize;
        int margin = fontSize;
        int charsPerLine = 32;
        int layer = 1;

        Movie movie = new Movie();
 
        int maxWidth = 0;
        int x = margin;
        int y = margin;

        int fontId = movie.newIdentifier();
        DefineFont2 definition = font.defineFont(fontId, set);
        Canvas path = new Canvas(false);

        movie.setFrameSize(new Bounds(0, 0, 0, 0));
        movie.setFrameRate(1.0f);
        movie.add(new Background(ColorTable.lightblue()));
        movie.add(definition);

        for (int i=0; i<alphabet.length(); i++)
        {
            DefineText2 text = null; //TODO TextFactory.defineText(movie.newIdentifier(), alphabet.substring(i,i+1), definition, fontSize, ColorTable.black());

            int textWidth = text.getBounds().getWidth();
            int textHeight = text.getBounds().getHeight();
            int advance = 0; //TODO TextFactory.boundsForText(alphabet.substring(i,i+1), definition, fontSize).getWidth() + 40;

            int shapeId = movie.newIdentifier();

            path.clear();
            path.setLineStyle(new LineStyle(1, ColorTable.darkblue()));
            path.rect(text.getBounds().getMinX(), text.getBounds().getMinY(), textWidth, textHeight);

            movie.add(path.defineShape(shapeId));
            movie.add(Place2.show(shapeId, layer++, x+textWidth/2, y+textHeight/2));

            movie.add(text);
            movie.add(Place2.show(text.getIdentifier(), layer++, x, y));

            if (i % charsPerLine == charsPerLine-1)
            {
                maxWidth = x+advance+margin > maxWidth ? x+advance+margin : maxWidth;

                x = margin;
                y += lineSpacing;
            }
            else
            {
                x += advance;
            }
        }
        movie.setFrameSize(new Bounds(0, 0, maxWidth, y+margin));

        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(destFile.getPath());
	}
}
