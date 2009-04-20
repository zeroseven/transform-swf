package debug;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.factory.image.ImageFactory;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.datatype.ColorTable;

import java.io.*;
import java.util.zip.DataFormatException;

public final class ShowImage
{
    public static void main(String[] args)
    {
        String sourceFile = args[0];
        String destFile = args[1];
        Movie movie;

        try
        {
        	movie = new Movie();
        	ImageTag image = ImageFactory.defineImage(movie.newIdentifier(), sourceFile);

            int imageWidth = (image).getWidth();
            int imageHeight = (image).getHeight();
            int screenWidth = imageWidth*20;
            int screenHeight = imageHeight*20;

            int shapeId = movie.newIdentifier();

            movie.setFrameRate(1.0f);
            movie.setSignature("FWS");
            movie.setFrameSize(new Bounds(-screenWidth/2, -screenHeight/2, screenWidth/2, screenHeight/2));

            movie.add(new Background(ColorTable.lightblue()));
            movie.add(image);
            movie.add(ImageFactory.defineEnclosingShape(shapeId, image, imageWidth/2, imageHeight/2, null));
            movie.add(Place2.show(shapeId, 1, 0, 0));
            movie.add(ShowFrame.getInstance());

            movie.encodeToFile(destFile);
        }
        catch (DataFormatException e)
        {
            System.err.println(e.toString());
        }
        catch (CoderException e)
        {
            System.err.println(e.toString());
        }
        catch (IOException e)
        {
            System.err.println(e.toString());
        }
    }
}
