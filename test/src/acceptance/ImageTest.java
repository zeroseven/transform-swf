package acceptance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.DataFormatException;

import com.flagstone.transform.factory.image.ImageFactory;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.ColorTable;
import com.flagstone.transform.movie.shape.DefineShape3;

public class ImageTest
{	
	protected void showFiles(File sourceDir, String[] files, File destDir) throws IOException, DataFormatException
    {
        File sourceFile;
        File destFile;  
        ImageTag image;
        
        if (destDir.exists() == false && destDir.mkdirs() == false) {
        	throw new FileNotFoundException();
        }
        
    	for (String file : files)
        {
        	sourceFile = new File(sourceDir, file);
        	destFile = new File(destDir, file.substring(0, file.lastIndexOf('.')) + ".swf");
        	
       		image = ImageFactory.defineImage(10, sourceFile);
            showImage(image, destFile);
        }
    }
    
    protected void showImage(ImageTag image, File file) throws IOException, DataFormatException
    {
        Movie movie = new Movie();

        int xOrigin = (image).getWidth()/2;
        int yOrigin = (image).getHeight()/2;

        DefineShape3 shape = ImageFactory.defineEnclosingShape(movie.newIdentifier(), image, -xOrigin, -yOrigin, null);

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
