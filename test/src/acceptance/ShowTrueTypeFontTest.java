package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.DataFormatException;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.factory.font.Font;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.ColorTable;
import com.flagstone.transform.movie.font.DefineFont2;
import com.flagstone.transform.movie.text.DefineTextField;

public final class ShowTrueTypeFontTest
{
	private static FilenameFilter filter;
	private static Set<Character> set;
	private static String alphabet;

	private static int width = 8000;
	private static int height = 4000;
	private static int margin = 400;
	private static int fontSize = 280;   
    
	private static int screenWidth = width + margin;
	private static int screenHeight = height + margin;

	@BeforeClass
	public static void setup()
	{
        filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".ttf");
            }
        };
		
    	set = new LinkedHashSet<Character>();
    	alphabet = "abcdefghijklmnopqrstuvwxyz";
    	
    	for (int i=0; i<alphabet.length(); i++) {
    		set.add(alphabet.charAt(i));
    	}   	
	}
	
    @Test
    public void showBitstream() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/ttf/reference/bitstream-vera");
        File destDir = new File("test/results/ShowTrueTypeFontTest/ttf/bitstream-vera");
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
    
    @Test
    public void showDejaVu() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/ttf/reference/dejavu");
        File destDir = new File("test/results/ShowTrueTypeFontTest/ttf/dejavu");
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
    
    private void showFiles(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
    {
        File sourceFile;
        File destFile;  
    	Font font;
    	DefineFont2 definition;
        
        if (!destDir.exists() && !destDir.mkdirs()) {
        	fail();
        }
        
        for (String file : files)
        {
        	sourceFile = new File(sourceDir, file);
        	destFile = new File(destDir, file.substring(0, file.lastIndexOf('.')) + ".swf");
        	font = new Font();
        	//TODOfont.decode(sourceFile);
            definition = font.defineFont(10, set);
            showFont(definition, destFile);
        }
    }
    
    private void showFont(DefineFont2 font, File file) throws IOException, DataFormatException
    {
        Movie movie = new Movie();

        DefineTextField text = new DefineTextField(movie.newIdentifier());
        text.setBounds(new Bounds(0, 0, width, height));
        text.setVariableName("var");
        text.setInitialText(alphabet);
        text.setUseFontGlyphs(true);
        text.setFontIdentifier(font.getIdentifier());
        text.setFontHeight(fontSize);
        text.setColor(ColorTable.black());
        
        movie.setFrameSize(new Bounds(0, 0, screenWidth, screenHeight));
        movie.setFrameRate(1.0f);
        movie.add(new Background(ColorTable.lightblue()));
        movie.add(font);
        movie.add(text);
        movie.add(Place2.show(text.getIdentifier(), 1, margin , margin));
        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(file.getPath());
    }
}
