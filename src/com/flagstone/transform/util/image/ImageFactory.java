/*
 *  ImageConstructor.java
 *  Transform Utilities
 *
 * Copyright (c) 2001-2009 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.util.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import com.flagstone.transform.Movie;
import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.FillStyle;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.fillstyle.BitmapFill;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;
import com.flagstone.transform.video.ImageBlock;

/**
 * <p>Image is used to generate an image definition object from an image stored
 * in a file. An plug-in architecture allows decoders to be registered to handle
 * different image formats.</p>
 * 
 * <p>Currently PNG, BMP and JPEG encoded images are supported using the classes 
 * provided in the Transform framework. New decoders can be added by implementing
 * the ImageDecoder interface and registering them using the registerDecoder()
 * method.</p>
 * 
 * <P>The defineImage() methods return an Definition (the abstract base class 
 * for all objects used to define shapes etc. in a Flash file. The exact class 
 * of the object generated depends of the format of the image loaded.</P>
 *
 * <table>
 * <tr><th>Class</th><th>Generated when...</th></tr>
 *
 * <tr>
 * <td valign="top">DefineJPEGImage2</td>
 * <td>A JPEG encoded image is loaded. The getFormat() method returns the class constant JPEG.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">DefineImage</td>
 * <td>An indexed BMP or PNG image contains a colour table without transparent colours or 
 * when a true colour image contains 16-bit or 24-bit colours is loaded. The getFormat() method returns 
 * the class constants IDX8, RGB5 or RGB8.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">DefineImage2</td>
 * <td>A BMP or PNG indexed image contains a colour table with transparent colours is loaded or 
 * when a true colour image contains 32-bit bit colours. The getFormat() method returns the class 
 * constants IDXA or RGBA.</td>
 * </tr>
 *
 * </table>
 * 
 * <P>Images are displayed in Flash by filling a shape with the image bitmap. 
 * The defineEnclosingShape() method generates a rectangular shape object which 
 * wraps the image:
 *
 * <pre>
 *     int imageId = movie.newIdentifier();
 *     int shapeId = movie.newIdentifier();
 * 
 *     Definition image = Image(defineImage(imageId, ...);
 *     
 *     int xOrigin = image.getWidth()/2;
 *     int yOrigin = image.getHeight()/2;
 * 
 *     LineStyle style = new LineStyle(20, ColorTable.black());
 * 
 *     movie.add(image);
 *     movie.add(Image.defineEnclosingShape(shapeId, image, xOrigin, yOrigin, style);
 * </pre>
 * 
 * <P>Here the origin, used when placing the shape on the screen, is defined as 
 * the centre of the shape. Other points may be defined to suit the alignment of 
 * the shape when it is placed on the display list.</P>
 * 
 * <p>Image also supports conversion of images to and from BufferedImage object
 * opening up a range of tools in the JDK for performing image processing.</p>
 * 
 * @see java.awt.image.BufferedImage
 * @see javax.imageio.ImageIO
 */
public final class ImageFactory
{    
	/**
	 * Create an image definition for the image located at the specified path.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * image in the Flash file.
	 * 
	 * @param path the path to the file containing the image.
	 * 
	 * @return an image definition that can be added to a Movie.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the image, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the image.
	 */
    public static ImageTag defineImage(int identifier, String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	return defineImage(identifier, new File(path));
    }
    
	/**
	 * Create an image definition for the image located in the specified file.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * image in the Flash file.
	 * 
	 * @param file a file containing the abstract path to the image.
	 * 
	 * @return an image definition that can be added to a Movie.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the image, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the image.
	 */
    public static ImageTag defineImage(int identifier, File file) throws FileNotFoundException, IOException, DataFormatException
    {
     	ImageInfo info = new ImageInfo();
    	info.setInput(new RandomAccessFile(file, "r"));
    	info.setDetermineImageNumber(true);
    	
    	if (!info.check()) 
    	{
    		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
    	}
    	
    	ImageDecoder provider = ImageRegistry.getImageProvider(info.getImageFormat());
    	provider.read(file);
    	return provider.defineImage(identifier);
    }

	/**
	 * Create an image definition for the image referenced by a URL.
	 * 
	 * @param identifier the unique identifier that will be used to refer to the 
	 * image in the Flash file.
	 * 
	 * @param url the Uniform Resource Locator referencing the file.
	 * 
	 * @return an image definition that can be added to a Movie.
	 * 
	 * @throws FileNotFoundException if the file cannot be found or opened.
	 * 
	 * @throws IOException if there is an error reading the file.
	 * 
	 * @throws DataFormatException if there is a problem decoding the image, 
	 * either it is in an unsupported format or an error occurred while decoding
	 * the image.
	 */
    public static ImageTag defineImage(int identifier, URL url) throws FileNotFoundException, IOException, DataFormatException
    {
	    URLConnection connection = url.openConnection();

	    int fileSize = connection.getContentLength();
            
	    if (fileSize<0) {
              throw new FileNotFoundException(url.getFile());
	    }
	    
	    byte[] bytes = new byte[fileSize];

	    InputStream stream = url.openStream();
	    BufferedInputStream buffer = new BufferedInputStream(stream);

	    buffer.read(bytes);
	    buffer.close();

    	ImageInfo info = new ImageInfo();
    	info.setInput(new ByteArrayInputStream(bytes));
    	info.setDetermineImageNumber(true);
    	
    	if (!info.check())  {
    		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
    	}
    	
    	ImageDecoder provider = ImageRegistry.getImageProvider(info.getImageFormat());
    	provider.read(url);
    	return provider.defineImage(identifier);
    }

    /**
     * Generates the shape definition object that is required to display an image in a Flash movie.
     * The shape is generated with a single fill style (BitmapFill object). The origin of the shape
     * is specified relative to the top left corner of the image.
     *
     * The borderStyle argument specifies a border that will be drawn around the image. The style
     * may be set to null is no border is drawn.
     * 
     * @param identifier an unique identifier that is used to reference the shape definition in a  
     * Flash movie.
     *
     * @param image the unique identifier of the image generated using the defineImage() method.
     * 
     * @param xOrigin the offset in pixels along the x-axis, relative to the top left corner of 
     * the image, where the origin (0,0) of the shape will be located.
     * 
     * @param yOrigin the offset in pixels along the y-axis, relative to the top left corner of 
     * the image, where the origin (0,0) of the shape will be located.
     *
     * @param borderStyle the style drawn around the border of the image. May be null if no 
     * border is drawn.
     */
    public static DefineShape3 defineEnclosingShape(int identifier, ImageTag image, int xOrigin, int yOrigin, LineStyle borderStyle)
    {
    	int width = (image).getWidth();
    	int height = (image).getHeight();
        int lineWidth = 0;
                
        if (borderStyle != null) {
            lineWidth = borderStyle.getWidth() / 2;
        }
        
        Bounds bounds = new Bounds(-xOrigin*20-lineWidth, -yOrigin*20-lineWidth, 
            (width-xOrigin)*20+lineWidth, (height-yOrigin)*20+lineWidth);

        Shape shape = new Shape(new ArrayList<ShapeRecord>());
        ShapeStyle style = new ShapeStyle().setLineStyle(borderStyle == null ? 0 : 1).setFillStyle(1); 
        style.setMove(-xOrigin*20, -yOrigin*20);
    
        shape.add(style);
        shape.add(new Line(width*20, 0));
        shape.add(new Line(0, height*20));
        shape.add(new Line(-width*20, 0));
        shape.add(new Line(0, -height*20));
        
        DefineShape3 definition = new DefineShape3(identifier, bounds, new ArrayList<FillStyle>(), new ArrayList<LineStyle>(), shape);
        CoordTransform transform = new CoordTransform(20.0f, 20.0f, 0, 0, -xOrigin*20, -yOrigin*20);
    
        if (borderStyle != null) {
            definition.add(borderStyle);
        }
        
        definition.add(new BitmapFill(false, false, image.getIdentifier(), transform));

        return definition;
    }
      	
	/**
     * Return an image stored in a a file as an array of ImageBlock objects that 
     * can be used when creating ScreenVideo streams.
     * 
     * The image is divided by tiling blocks of the specified width and height 
     * across the image. For blocks at the right and bottom edges the size of 
     * the block may be reduced so that it fits the image exactly. In other 
     * words the blocks are not padded with extra pixel information.
     * 
     * @param blockWidth the width of a block in pixels.
     * @param blockHeight the height of a block in pixels
     * @param file the File containing the abstract path to the image.
     * @return an array of FMImageBlock objects.
     */   
	public static void getImageAsBlocks(byte[]image, int width, int height, List<ImageBlock>blocks, int blockWidth, int blockHeight)
	{
    	int row = 0;
        int col = 0;
        
        int src = 0;
        int dst = 0;

        byte[] formattedImage = new byte[width*height*3];
        
        for (row=height-1; row>=0; row--)
        {
        	src = row*width;
        	
            for (col=0; col<width; col++, src+=4)
            {
                formattedImage[dst++] = image[src+2];
                formattedImage[dst++] = image[src+1];
                formattedImage[dst++] = image[src];
            }
        }

		int columns = (width+blockWidth-1)/blockWidth;
		int rows = (height+blockHeight-1)/blockHeight;
		
		byte[] blockData = new byte[blockHeight*blockWidth*3];
		
		for (int i=0; i<rows; i++)
		{
			for (int j=0; j<columns; j++)
			{
				int xOffset = j*blockWidth;
				int yOffset = i*blockHeight;
				
				int xSpan = (width-xOffset > blockWidth) ? blockWidth : width-xOffset;
				int ySpan = (height-yOffset > blockHeight) ? blockHeight : height-yOffset;
				int offset = 0;
				
				int idx;
				
				for (int k=0; k<ySpan; k++) 
				{
					for (int l=0; l<xSpan; l++, offset+=3) 
					{
						idx = (yOffset+k)*(width*3)+(xOffset+l)*3;
						
						blockData[offset] = formattedImage[idx];					
						blockData[offset+1] = formattedImage[idx+1];					
						blockData[offset+2] = formattedImage[idx+2];					
					}
				}
				
				blocks.add(new ImageBlock(xSpan, ySpan, zip(blockData, offset)));
			}
		}
	}

	private static byte[] zip(byte[] image, int length)
	{
		Deflater deflater = new Deflater();
		deflater.setInput(image, 0, length);
		deflater.finish();

		byte[] compressedData = new byte[image.length];
		int bytesCompressed = deflater.deflate(compressedData);
		byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);

		return newData;
	}
}
