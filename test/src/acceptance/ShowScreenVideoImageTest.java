package acceptance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.List;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import org.junit.Test;


import com.flagstone.transform.Background;
import com.flagstone.transform.DefineVideo;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.Strings;
import com.flagstone.transform.VideoFrame;
import com.flagstone.transform.DefineVideo.Deblocking;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.ColorTable;
import com.flagstone.transform.datatype.VideoFormat;
import com.flagstone.transform.datatype.VideoFrameType;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.util.image.ImageDecoder;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageInfo;
import com.flagstone.transform.util.image.ImageRegistry;
import com.flagstone.transform.video.ImageBlock;
import com.flagstone.transform.video.ScreenPacket;

public final class ShowScreenVideoImageTest
{
    @Test
    public void showPNG() throws IOException, DataFormatException
    {
        File sourceDir = new File("test/data/png-screenshots");
        File destDir = new File("test/results/ShowScreenVideoImageTest");
        
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File directory, String name) {
                return name.endsWith(".png");
            }
        };
        
        String[] files = sourceDir.list(filter);
        
  		Movie movie;
  		MovieTag image;

		File destFile = null;
 		
        int blockWidth = 64;
        int blockHeight = 64;

        int screenWidth;
        int screenHeight;

        int numberOfFrames = files.length;
        Deblocking deblocking = Deblocking.OFF;
        boolean smoothing = false;
        VideoFormat codec = VideoFormat.SCREEN;
        int identifier;

     	ImageInfo info = new ImageInfo();
    	info.setInput(new RandomAccessFile(new File(sourceDir, files[0]), "r"));
    	info.setDetermineImageNumber(true);
    	
    	if (!info.check()) 
    	{
    		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
    	}
    	
    	ImageDecoder provider = ImageRegistry.getImageProvider(info.getImageFormat());
    	provider.read(new File(sourceDir, files[0]));

    	image = ImageFactory.defineImage(0, new File(sourceDir, files[0]));
	
	    screenWidth = ((ImageTag)image).getWidth();
	    screenHeight = ((ImageTag)image).getHeight();
	    
	    movie = new Movie();
	    identifier = movie.newIdentifier();
	    
	    movie.setFrameSize(new Bounds(0,0,screenWidth*20,screenHeight*20));
	    movie.setFrameRate(4.0f);
	    movie.add(new Background(WebPalette.ALICE_BLUE.color()));
	                          
        movie.add(new DefineVideo(identifier, numberOfFrames, screenWidth, screenHeight, deblocking, smoothing, codec));     
        
        List<ImageBlock> prev = new ArrayList<ImageBlock>();
        List<ImageBlock> next = new ArrayList<ImageBlock>();
        List<ImageBlock> delta = new ArrayList<ImageBlock>();
        
        ImageFactory.getImageAsBlocks(provider.getImage(), provider.getWidth(), provider.getHeight(), prev, blockWidth, blockHeight);

        ScreenPacket packet = new ScreenPacket(true, screenWidth, screenHeight, blockWidth, blockHeight, prev);
        
    	movie.add(Place2.show(identifier, 1, 0,0));
        movie.add(new VideoFrame(identifier, 0, packet.encode()));
        movie.add(ShowFrame.getInstance());
  
    	Place2 place; 

    	for (int i=1; i<numberOfFrames; i++)
    	{
            File srcFile = new File(sourceDir, files[i]);

        	info.setInput(new RandomAccessFile(srcFile, "r"));
        	info.setDetermineImageNumber(true);
        	
        	if (!info.check()) 
        	{
        		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        	}
        	
        	provider = ImageRegistry.getImageProvider(info.getImageFormat());
        	provider.read(srcFile);

        	ImageFactory.getImageAsBlocks(provider.getImage(), provider.getWidth(), provider.getHeight(), next, blockWidth, blockHeight);
            
    		delta = new ArrayList<ImageBlock>(prev.size());
    		
    		for (int j=0; j<prev.size(); j++)
    		{
    			if (prev.get(j).equals(next.get(j))) {
    				delta.add(new ImageBlock(0,0,null));
    			}
    			else {
    				delta.add(next.get(j));
    			}
    		}
            
        	packet = new ScreenPacket(false, screenWidth, screenHeight, blockWidth, blockHeight, delta);
            place = Place2.move(1, 0, 0);
            place.setRatio(i);
            
        	movie.add(place);
            movie.add(new VideoFrame(identifier, i, packet.encode()));
            movie.add(ShowFrame.getInstance());
    	}
            
	    destFile = new File(destDir, sourceDir.getName()+".swf");
	    movie.encodeToFile(destFile.getAbsolutePath());
    }
}
