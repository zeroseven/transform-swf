package acceptance;

import java.io.File;


import com.flagstone.transform.factory.shape.Canvas;
import com.flagstone.transform.movie.Background;
import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.Place2;
import com.flagstone.transform.movie.ShowFrame;
import com.flagstone.transform.movie.datatype.ColorTable;
import com.flagstone.transform.movie.fillstyle.SolidFill;
import com.flagstone.transform.movie.linestyle.LineStyle;
import com.flagstone.transform.movie.shape.DefineShape2;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

public final class CanvasTest
{
	private static File resultDir;
	private static Canvas path;
	
	private static int width;
	private static int height;
	
	@BeforeClass
	public static void initialize()
	{
		path = new Canvas(true);
        
        resultDir = new File("test/results/CanvasTest");
        
        if (resultDir.exists() == false && resultDir.mkdirs() == false) {
        	fail();
        }

        width = 150;
        height = 100;
	}
	
	@Before
	public void setup()
	{
		path.clear();
        path.setLineStyle(new LineStyle(20, ColorTable.black()));
        path.setFillStyle(new SolidFill(ColorTable.red()));
	}
	
	@Test
    public void rectangle()
    {
		File destFile = new File(resultDir, "rectangle.swf");
        path.rect(width/2, -height/2, width, height);
        showShape(path.defineShape(1), destFile);
    }
	
	@Test
    public void roundedRectangle()
    {
		File destFile = new File(resultDir, "rounded.swf");		
        path.rect(width/2, height/2, width, height, 10);
        showShape(path.defineShape(1), destFile);
    }
	
	@Test
    public void circle()
    {
		File destFile = new File(resultDir, "circle.swf");
        path.circle(-width/2, height/2, height/2);
        showShape(path.defineShape(1), destFile);
    }
	
	@Test
    public void ellipse()
    {
		File destFile = new File(resultDir, "ellipse.swf");
        path.ellipse(-width/2, -height/2, width/2, height/2);
        showShape(path.defineShape(1), destFile);
    }
	
	@Test
    public void rpolyline()
    {
		File destFile = new File(resultDir, "rpolyline.swf");
		
        int[] points = new int[] {
            0, -100, 10, 0, 0, 90, 90, 0, 0, 20,
            -90, 0, 0, 90, -20, 0, 0, -90, -90, 0,
            0, -20, 90, 0, 0, -90, 10, 0
        };

        path.rpolygon(points);
        showShape(path.defineShape(1), destFile);
    }
	
	@Test
    public void curve()
    {
		File destFile = new File(resultDir, "curve.swf");		
        path.curve(0, -100, 150, -100, 150, 0);
        path.close();
        showShape(path.defineShape(1), destFile);
    }
   
    private void showShape(DefineShape2 shape, File file)
    {
    	try
    	{
	        Movie movie = new Movie();
	        movie.setFrameRate(1.0f);
	        movie.setFrameSize(shape.getBounds());
	        movie.add(new Background(ColorTable.lightblue()));

	        movie.add(shape);
	        movie.add(new Place2(shape.getIdentifier(), 1, 0, 0));
	        movie.add(ShowFrame.getInstance());
	        
	        movie.encodeToFile(file);
    	}
    	catch (Throwable t)
    	{
    		fail();
    	}
     }
}
