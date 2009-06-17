package acceptance;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.fillstyle.SolidFill;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.shape.DefineShape2;
import com.flagstone.transform.util.shape.Canvas;

public final class CanvasTest {
    private static File resultDir;
    private static Canvas path;

    private static int width;
    private static int height;

    @BeforeClass
    public static void initialize() {
        path = new Canvas(true);

        resultDir = new File("test/results/acceptance/CanvasTest");

        if (!resultDir.exists() && !resultDir.mkdirs()) {
            fail();
        }

        width = 150;
        height = 100;
    }

    @Before
    public void setUp() throws DataFormatException, IOException {
        path.clear();
        path.setLineStyle(new LineStyle(20, WebPalette.BLACK.color()));
        path.setFillStyle(new SolidFill(WebPalette.RED.color()));
    }

    @Test
    public void rectangle() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "rectangle.swf");
        path.rect(width / 2, -height / 2, width, height);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void roundedRectangle() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "rounded.swf");
        path.rect(width / 2, height / 2, width, height, 10);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void circle() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "circle.swf");
        path.circle(-width / 2, height / 2, height / 2);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void ellipse() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "ellipse.swf");
        path.ellipse(-width / 2, -height / 2, width / 2, height / 2);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void rpolyline() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "rpolyline.swf");

        final int[] points = new int[] { 0, -100, 10, 0, 0, 90, 90, 0, 0, 20,
                -90, 0, 0, 90, -20, 0, 0, -90, -90, 0, 0, -20, 90, 0, 0, -90,
                10, 0 };

        path.rpolygon(points);
        showShape(path.defineShape(1), destFile);
    }

    @Test
    public void curve() throws DataFormatException, IOException {
        final File destFile = new File(resultDir, "curve.swf");
        path.curve(0, -100, 150, -100, 150, 0);
        path.close();
        showShape(path.defineShape(1), destFile);
    }

    private void showShape(final DefineShape2 shape, final File file)
            throws DataFormatException, IOException {
        final Movie movie = new Movie();
        movie.setFrameRate(1.0f);
        movie.setFrameSize(shape.getBounds());
        movie.add(new Background(WebPalette.LIGHT_BLUE.color()));

        movie.add(shape);
        movie.add(Place2.show(shape, 1, 0, 0));
        movie.add(ShowFrame.getInstance());

        movie.encodeToFile(file);
    }
}
