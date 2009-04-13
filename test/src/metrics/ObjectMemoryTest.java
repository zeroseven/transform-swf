package metrics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import com.flagstone.transform.factory.movie.ActionFactory;
import com.flagstone.transform.factory.movie.MovieFactory;
import com.flagstone.transform.movie.MovieTag;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.action.Action;

public final class ObjectMemoryTest
{
    private static PrintWriter writer;
    private static File resultDir;
    private static File resultFile;
       
    @BeforeClass
    public static void initialize() throws IOException
    {
		resultDir = new File("test/results/ObjectMemoryTest");
		resultFile = new File(resultDir, "memory-use.txt");
		
		if (resultDir.exists() == false && resultDir.mkdirs() == false) {
			fail();
		}
		
    	writer = new PrintWriter(resultFile);    	
		writer.append(String.format("class,memory%n"));
    }
    
    @AfterClass
    public static void report() throws IOException
    {
    	writer.close();    	
    }

    @Test
    public void memory()
    {
    	MovieTags();
    	Actions();
    }

    private void MovieTags()
    {	
    	int[] types = new int[] {
			Types.SHOW_FRAME,
			Types.DEFINE_SHAPE,
			Types.PLACE,
			Types.REMOVE,
			Types.DEFINE_JPEG_IMAGE,
			Types.DEFINE_BUTTON,
			Types.JPEG_TABLES,
			Types.SET_BACKGROUND_COLOR,
			Types.DEFINE_FONT,
			Types.DEFINE_TEXT,
			Types.DO_ACTION,
			Types.FONT_INFO,
			Types.DEFINE_SOUND,
			Types.START_SOUND,
			Types.SOUND_STREAM_HEAD,
			Types.SOUND_STREAM_BLOCK,
			Types.BUTTON_SOUND,
			Types.DEFINE_IMAGE,
			Types.DEFINE_JPEG_IMAGE_2,
			Types.DEFINE_SHAPE_2,
			Types.BUTTON_COLOR_TRANSFORM,
			Types.PROTECT,
			Types.FREE,
			Types.PLACE_2,
			Types.REMOVE_2,
			Types.DEFINE_SHAPE_3,
			Types.DEFINE_TEXT_2,
			Types.DEFINE_BUTTON_2,
			Types.DEFINE_JPEG_IMAGE_3,
			Types.DEFINE_IMAGE_2,
			Types.DEFINE_MOVIE_CLIP,
			Types.FRAME_LABEL,
			Types.SOUND_STREAM_HEAD_2,
			Types.DEFINE_MORPH_SHAPE,
			Types.DEFINE_FONT_2,
			Types.PATHS_ARE_POSTSCRIPT,
			Types.DEFINE_TEXT_FIELD,
			Types.QUICKTIME_MOVIE,
			Types.SERIAL_NUMBER,
			Types.ENABLE_DEBUGGER,
			Types.EXPORT,
			Types.IMPORT,
			Types.INITIALIZE,
			Types.DEFINE_VIDEO,
			Types.VIDEO_FRAME,
			Types.FONT_INFO_2,
			Types.ENABLE_DEBUGGER_2,
			Types.LIMIT_SCRIPT,
			Types.TAB_ORDER,
    	};
    	
     	long before = 0;
    	long after = 0;
    	
    	MovieFactory factory = new MovieFactory();
		MovieTag obj;
		
        for (int i=0; i<types.length; i++)  
        {
			obj = factory.getObjectOfType(types[i]);
            obj = null;
        }

        for (int i=0; i<types.length; i++)  
        {
			obj = factory.getObjectOfType(types[i]);
			
            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            obj = null;

            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            
            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

			obj = factory.getObjectOfType(types[i]);

            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
              
            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            writer.append(obj.getClass().getName()).append(',');
            writer.print(after-before);
            writer.append('\n');
        }
	}
        
    private void Actions()
    {	
    	int[] types = new int[] {
			Types.ADD, 
			Types.GET_URL, 
			Types.GOTO_FRAME, 
			Types.GOTO_LABEL, 
			Types.SET_TARGET,
			Types.WAIT_FOR_FRAME,
			Types.CALL,
			Types.PUSH,
			Types.WAIT_FOR_FRAME_2,
			Types.JUMP,
			Types.IF,
			Types.GET_URL_2,
			Types.GOTO_FRAME_2,
			Types.TABLE,
			Types.REGISTER_COPY,
			Types.NEW_FUNCTION,
			Types.WITH,
			Types.EXCEPTION_HANDLER,
			Types.NEW_FUNCTION_2,
    	};
    	
     	long before = 0;
    	long after = 0;

    	ActionFactory factory = new ActionFactory();
		Action obj;
		
        for (int i=0; i<types.length; i++)  
        {
			obj = factory.getObjectOfType(types[i]);
            obj = null;
        }

        for (int i=0; i<types.length; i++)  
        {
			obj = factory.getObjectOfType(types[i]);
			
            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            obj = null;

            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            
            before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

			obj = factory.getObjectOfType(types[i]);

            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
              
            after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            writer.append(obj.getClass().getName()).append(',');
            writer.print(after-before);
            writer.append('\n');
        }
	}
}
