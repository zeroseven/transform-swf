package debug;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Movie;
import com.flagstone.transform.coder.CoderException;

public final class EncodeMovie
{
    public static void main(String[] args)
    {
        String sourceFile = args[0];
        String destFile = args[1];
        Movie movie;

        try
        {
            movie = new Movie();
            movie.decodeFromFile(new File(sourceFile));
            movie.encodeToFile(new File(destFile));
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