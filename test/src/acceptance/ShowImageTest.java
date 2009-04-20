package acceptance;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

import java.util.zip.DataFormatException;


import com.flagstone.transform.factory.image.ImageFactory;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.ColorTable;
import com.flagstone.transform.movie.linestyle.LineStyle;
import com.flagstone.transform.movie.shape.DefineShape3;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.Ignore;

public final class ShowImageTest
{
    @Test
    public void showBMP() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/bmp/reference");
        File destDir = new File("test/results/ShowImageTest/bmp");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".bmp");
            }
        };
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
    
    @Test
    public void showPNG() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/png/reference");
        File destDir = new File("test/results/ShowImageTest/png");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".png");
            }
        };
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
    
    @Test @Ignore
    public void showJPG() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/jpg/reference");
        File destDir = new File("test/results/ShowImageTest/jpg");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".jpg");
            }
        };
        
        showFiles(sourceDir, sourceDir.list(filter), destDir);
    }
    
    private void showFiles(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
    {
        File sourceFile;
        File destFile;  
        ImageTag image;
        
        if (destDir.exists() == false && destDir.mkdirs() == false) {
        	fail();
        }
        
        for (String file : files)
        {
        	sourceFile = new File(sourceDir, file);
        	destFile = new File(destDir, file.substring(0, file.lastIndexOf('.')) + ".swf");
        	image = ImageFactory.defineImage(10, sourceFile);
            showImage(image, destFile);
        }
    }
    
    private void showImage(ImageTag image, File file) throws IOException, DataFormatException
    {
        Movie movie = new Movie();

        int xOrigin = (image).getWidth()/2;
        int yOrigin = (image).getHeight()/2;

        LineStyle borderStyle = new LineStyle(20, ColorTable.black());

        DefineShape3 shape = ImageFactory.defineEnclosingShape(movie.newIdentifier(), image, -xOrigin, -yOrigin, borderStyle);

        movie.setFrameRate(1.0f);
        movie.setFrameSize(shape.getBounds());
        movie.add(new Background(ColorTable.lightblue()));
        movie.add(image);
        movie.add(shape);
        movie.add(Place2.show(shape.getIdentifier(), 1, 0, 0));
        movie.add(ShowFrame.getInstance());
        movie.encodeToFile(file.getPath());
    }
}
