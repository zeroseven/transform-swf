/*
 *  ShapeConstructor.java
 *  Transform Utilities
 *
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.factory.shape;

import java.util.ArrayList;
import java.util.List;

import com.flagstone.transform.movie.Movie;
import com.flagstone.transform.movie.datatype.Bounds;
import com.flagstone.transform.movie.fillstyle.FillStyle;
import com.flagstone.transform.movie.linestyle.LineStyle;
import com.flagstone.transform.movie.shape.Curve;
import com.flagstone.transform.movie.shape.DefineShape2;
import com.flagstone.transform.movie.shape.DefineShape3;
import com.flagstone.transform.movie.shape.Line;
import com.flagstone.transform.movie.shape.Shape;
import com.flagstone.transform.movie.shape.ShapeRecord;
import com.flagstone.transform.movie.shape.ShapeStyle;


/**
 * <p>The Canvas class is used to create shape definitions. Arbitrary paths can be 
 * created using a series of move, line or curve segments. Drawing operations 
 * using both absolute coordinates and coordinates relative to the current point 
 * (updated after every operation) are supported.</p>
 * 
 * <p>For curves both cubic and quadratic curves are supported. Flash only 
 * supports quadratic curves so cubic curves are approximated by a series of 
 * line segments using (converting cubic to quadratic curves is mathematically 
 * difficult). The smoothness of cubic curves is controlled by the flatness
 * attribute which can be used to limit the number of line segments that are 
 * drawn.</p>
 * 
 * <p>As a path is drawn the maximum and minimum x and y coordinates are 
 * recorded so that the bounding rectangle that completely encloses the shape 
 * can be defined. This is used when creating shape definitions using the 
 * DefineShape, DefineShape2 or DefineShape3 classes.<p>
 * 
 * <p>The Canvas class also supports a number of method to create closed paths 
 * that represent different geometric shapes. Basic rectangles, ellipses and 
 * circles are supported. More complex shapes can be drawn using the polygon() 
 * method which uses pairs of points to specified the vertices of an arbitrary 
 * shapes.</p>
 * 
 * <p>When drawing paths whether coordinates are specified in twips or pixels is 
 * set when the Canvas object is created. When specifying coordinates in pixels
 * all coordinates are converted internally to twips to perform the actual 
 * drawing.</p>
 * 
 * <p>The following code samples illustrate how to use the Canvas class create 
 * shapes.</p>
 *
 *<pre>
 *    Canvas path = new Canvas(true); // coordinates are in pixels.
 * 
 *    int width = 200;
 *    int height = 100;
 *
 *    newPath(); 
 *    setLineStyle(new LineStyle(1, ColorTable.black()));
 *    setFillStyle(new SolidFill(ColorTable.red()));
 *    move(-width/2, -height/2);
 *    rline(width, 0); 
 *    rline(0, height);
 *    rline(-width, 0);
 *    rline(0, -height);
 *    closePath();
 * 
 *    DefineShape3 rect = path.defineShape(movie.newIdentifier());
 * 
 *</pre>
 */
public final class Canvas
{
    private static final double FLATTEN_LIMIT = 0.25;
    
    protected boolean arePixels;
    
    protected double[] cubicX = new double[4];
    protected double[] cubicY = new double[4];
    
    protected boolean pathInProgress = false;
    
    protected int initialX;
    protected int initialY;
    
    protected int currentX;
    protected int currentY;
    
    protected int controlX;
    protected int controlY;

    protected int minX;
    protected int minY;
    protected int maxX;
    protected int maxY;
    
    protected int lineWidth;
    
    protected  List<ShapeRecord> objects;
    protected  List<LineStyle> lineStyles;
    protected  List<FillStyle> fillStyles;
    
    /**
     * Creates an ShapeConstructor object with no path defined.
     * 
     * The pixels flag controls whether the coordinates passed to methods when 
     * creating a path of predefined shape are expressed in pixels (true) or 
     * twips (false).
     *
     * Flash coordinates are specified in twips (1 twip equals 1/1440th of an 
     * inch or 1/20th of a point). Allowing coordinates to be specified in pixels 
     * simplifies the drawing process avoiding the conversion to twips by 
     * multiplying each value by 20.
     * 
     * @param pixels coordinates are specified in pixels when true and twips 
     * when false.
     */
    public Canvas(boolean pixels)
    {
    	arePixels = pixels;
    	objects = new ArrayList<ShapeRecord>();
    	lineStyles = new ArrayList<LineStyle>();
    	fillStyles = new ArrayList<FillStyle>();
    }
    
    /**
     * Generates the bounding box that encloses the current path.
     */
    public Bounds getBounds()
    {
        return new Bounds(minX, minY, maxX, maxY);
    }

    /** 
     * Returns a copy of the array line styles.
     */
    public List<LineStyle> getLineStyles() 
    { 
    	List<LineStyle> array = new ArrayList<LineStyle>(lineStyles.size());
    	
    	for (LineStyle style : lineStyles) {
    		array.add(style.copy());
    	}
    	
    	return array; 
    }

    /** 
     * Returns a copy of the array fill styles.
     */
    public List<FillStyle> getFillStyles()
    {
    	List<FillStyle> array = new ArrayList<FillStyle>(fillStyles.size());
    	
    	for (FillStyle style : fillStyles) {
    		array.add(style.copy());
    	}

        return array;
    }
    
    /**
     * Returns the Shape object containing the objects used to draw the current path.
     *
     * @return an Shape object contain the Line, Curve and ShapeStyle objects used to 
     * construct the current path.
     */
    public Shape getShape()
    {
    	ArrayList<ShapeRecord> array = new ArrayList<ShapeRecord>(objects.size());
    	
    	for (ShapeRecord record : objects) {
    		array.add(record.copy());
    	}

        return new Shape(array);
    }


    /**
     * Set the style used to draw lines.
     * 
     * @param style a line style.
     */
    public void setLineStyle(LineStyle style)
    {
    	int index;
    	
    	if (lineStyles.contains(style)) {
    		index = lineStyles.indexOf(style);
    	}
    	else 
    	{
    		index = lineStyles.size();
	    	lineStyles.add(style.copy());
    	}
    	lineWidth = style.getWidth();
        objects.add(new ShapeStyle(index+1, Movie.VALUE_NOT_SET, Movie.VALUE_NOT_SET));
    }
    
    
    /**
     * Set the style used to fill enclosed areas.
     * 
     * @param style a fill style.
     */
    public void setFillStyle(FillStyle style)
    {
    	int index;
    	
    	if (fillStyles.contains(style)) {
    		index = fillStyles.indexOf(style);
    	}
    	else 
    	{
    		index = fillStyles.size();		
			fillStyles.add(style.copy());
    	}
        objects.add(new ShapeStyle(Movie.VALUE_NOT_SET, index+1, Movie.VALUE_NOT_SET));
    }
    
    
    /**
     * Set the style used to fill overlapping enclosed areas.
     * 
     * @param style a fill style.
     */
    public void setAltStyle(FillStyle style)
    {
    	int index;
    	
    	if (fillStyles.contains(style)) {
    		index = fillStyles.indexOf(style);
    	}
    	else 
    	{
    		index = fillStyles.size();
			fillStyles.add(style.copy());
    	}
        objects.add(new ShapeStyle(Movie.VALUE_NOT_SET, Movie.VALUE_NOT_SET, index+1));
    }
    
    /**
     * Generates a shape containing the current path and styles.
     *
     * The shape is constructed with copies of the style arrays and the shape 
     * representing the path drawn. This allows the number of styles to be 
     * changed without affecting previously created shapes.
     *
     * @param identifier an unique identifier for the shape.
     */
     public DefineShape2 defineShape(int identifier)
     {
    	return new DefineShape2(identifier, getBounds(), getFillStyles(), getLineStyles(), getShape());
     }
     
    /**
     * Generates a transparent shape containing the current path and styles.
     *
     * The shape is constructed with copies of the style arrays and the shape 
     * representing the path drawn. This allows the number of styles to be 
     * changed without affecting previously created shapes.
     *
     * @param identifier an unique identifier for the shape.
     */
     public DefineShape3 defineTransparentShape(int identifier)
     {
        return new DefineShape3(identifier, getBounds(), getFillStyles(), getLineStyles(), getShape());
     }
     
    /**
     * Creates a new path, discarding any path elements drawn.
     */
    public void clear()
    {
        pathInProgress = false;
        
        setInitial(0, 0);
        setCurrent(0, 0);
        setControl(0, 0);
        setBounds(0, 0, 0, 0);
        
        fillStyles.clear();
        lineStyles.clear();
        objects.clear();

        lineWidth = 0;
    }
    
    /**
     * Closes the current path by drawing a line from the current point to the 
     * starting point of the path. 
     */
    public void close()
    {
        int deltaX = initialX - currentX;
        int deltaY = initialY - currentY;
        
        if (deltaX != 0 || deltaY != 0) {
            objects.add(new Line(deltaX, deltaY));
        }
            
        setCurrent(initialX, initialY);
        pathInProgress = false;
    }
    
    /**
     * Move to the point (x,y).
     * 
     * @param xCoord the x-coordinate of the point to move to.
     * @param yCoord the y-coordinate of the point to move to.
     */
    public void move(int xCoord, int yCoord)
    {
        int pointX = arePixels ? xCoord * 20 : xCoord;
        int pointY = arePixels ? yCoord * 20 : yCoord;
        
        objects.add(new ShapeStyle(pointX, pointY));

        setControl((currentX+pointX)/2, (currentY+pointY)/2);
        setCurrent(pointX, pointY);
        setInitial(pointX, pointY);
    }
    
    public void moveForFont(int xCoord, int yCoord)
    {
        int pointX = arePixels ? xCoord * 20 : xCoord;
        int pointY = arePixels ? yCoord * 20 : yCoord;
        
        ShapeStyle style = new ShapeStyle(pointX, pointY); 

        if (objects.isEmpty()){
        	style.setFillStyle(1);
        }

        objects.add(style);        	

        setControl((currentX+pointX)/2, (currentY+pointY)/2);
        setCurrent(pointX, pointY);
        setInitial(pointX, pointY);
    }
  
    /**
     * Move relative to the current point.
     * 
     * @param xCoord the distance along the x-axis.
     * @param yCoord the distance along the y-axis.
     */
    public void rmove(int xCoord, int yCoord)
    {
        int pointX = arePixels ? xCoord * 20 : xCoord;
        int pointY = arePixels ? yCoord * 20 : yCoord;
        
        objects.add(new ShapeStyle(pointX+currentX, pointY+currentY)); 

        setControl(currentX+pointX/2, currentY+pointY/2);
        setCurrent(currentX+pointX, currentY+pointY);
   }
    
    /**
     * draw a line from the current point to the point (x,y).
     * 
     * @param xCoord the x-coordinate of the end of the line.
     * @param yCoord the y-coordinate of the end of the line.
     */
    public void line(int xCoord, int yCoord)
    {
        int pointX = (arePixels ? xCoord * 20 : xCoord) - currentX;
        int pointY = (arePixels ? yCoord * 20 : yCoord) - currentY;

        objects.add(new Line(pointX, pointY));
        
        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX+pointX/2, currentY+pointY/2);
        setCurrent(currentX+pointX, currentY+pointY);
    }
    
    /**
     * Draw a line relative to the current point.
     * 
     * @param xCoord the distance along the x-axis to the end of the line.
     * @param yCoord the distance along the y-axis to the end of the line.
     */
    public void rline(int xCoord, int yCoord)
    {
        int pointX = arePixels ? xCoord * 20 : xCoord;
        int pointY = arePixels ? yCoord * 20 : yCoord;

        objects.add(new Line(pointX, pointY));

        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX+pointX/2, currentY+pointY/2);
        setCurrent(currentX+pointX, currentY+pointY);
    }
    
    /**
     * Draw a quadratic bezier curve from the current point to the point (x,y) 
     * with the control point (x1, y1).
     * 
     * @param acontrolX the x-coordinate of the control point.
     * @param acontrolY the y-coordinate of the control point.
     * @param aanchorX the x-coordinate of the end of the curve.
     * @param aanchorY the y-coordinate of the end of the curve.
     */
    public void curve(int acontrolX, int acontrolY, int aanchorX, int aanchorY)
    {
        int rcontrolX = (arePixels ? acontrolX * 20 : acontrolX) - currentX;
        int rcontrolY = (arePixels ? acontrolY * 20 : acontrolY) - currentY;
        int ranchorX = (arePixels ? aanchorX * 20 : aanchorX) - currentX - rcontrolX;
        int ranchorY = (arePixels ? aanchorY * 20 : aanchorY) - currentY - rcontrolY;

        objects.add(new Curve(rcontrolX, rcontrolY, ranchorX, ranchorY));

        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
        setControl(currentX+rcontrolX, currentY+rcontrolY);
        setCurrent(currentX+rcontrolX+ranchorX, currentY+rcontrolY+ranchorY);
    }
    
    /**
     * Draw a quadratic bezier curve relative to the current point to the point.
     * 
     * @param rcontrolX the distance along the x-axis from the current point to the control point.
     * @param rcontrolY the distance along the y-axis from the current point to the control point.
     * @param ranchorX the distance along the x-axis from the current point to the end of the curve.
     * @param ranchorY the distance along the y-axis from the current point to the end of the curve.
     */
    public void rcurve(int rcontrolX, int rcontrolY, int ranchorX, int ranchorY)
    {
        int px1 = arePixels ? rcontrolX * 20 : rcontrolX;
        int py1 = arePixels ? rcontrolY * 20 : rcontrolY;
        int px2 = arePixels ? ranchorX * 20 : ranchorX;
        int py2 = arePixels ? ranchorY * 20 : ranchorY;

        objects.add(new Curve(px1, py1, px2, py2));

        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
 
        setControl(currentX+px1, currentY+py1);
        setCurrent(currentX+px1+px2, currentY+py1+py2);
    }
    
    /**
     * Draw a cubic bezier curve from the current point to the point (x,y) with 
     * the off-curve control points (x1, y1) and (x2, y2).
     * 
     * IMPORTANT: Converting cubic bezier curves to the quadratic bezier curves 
     * supported by Flash is mathematically difficult. The cubic curve is 
     * approximated by a series of straight line segments. 
     * 
     * @param cax the x-coordinate of the first control point.
     * @param cay the y-coordinate of the first control point.
     * @param cbx the x-coordinate of the second control point.
     * @param cby the y-coordinate of the second control point.
     * @param anx the x-coordinate of the end of the curve.
     * @param any the y-coordinate of the end of the curve.
     */
    public void curve(int cax, int cay, int cbx, int cby, int anx, int any)
    {
        cubicX[0] = currentX;
        cubicY[0] = currentY;
        cubicX[1] = arePixels ? cax * 20 : cax;
        cubicY[1] = arePixels ? cay * 20 : cay;
        cubicX[2] = arePixels ? cbx * 20 : cbx;
        cubicY[2] = arePixels ? cby * 20 : cby;
        cubicX[3] = arePixels ? anx * 20 : anx;
        cubicY[3] = arePixels ? any * 20 : any;
        
        flatten();
    }
    
    /**
     * Draw a cubic bezier curve relative to the current point.
     * 
     * IMPORTANT: Converting cubic bezier curves to the quadratic bezier curves 
     * supported by Flash is mathematically difficult. The cubic curve is 
     * approximated by a series of straight line segments. 
     * 
     * @param controlAX the distance along the x-axis from the current point to the first control point.
     * @param controlAY the distance along the y-axis from the current point to the first control point.
     * @param controlBX the distance along the x-axis from the current point to the second control point.
     * @param controlBY the distance along the y-axis from the current point to the second control point.
     * @param anchorX the distance along the x-axis from the current point to the end of the curve.
     * @param anchorY the distance along the y-axis from the current point to the end of the curve.
     */
    public void rcurve(int controlAX, int controlAY, int controlBX, int controlBY, int anchorX, int anchorY)
    {
        cubicX[0] = currentX;
        cubicY[0] = currentY;
        cubicX[1] = currentX + (arePixels ? controlAX * 20 : controlAX);
        cubicY[1] = currentY + (arePixels ? controlAY * 20 : controlAY);
        cubicX[2] = currentX + (arePixels ? controlBX * 20 : controlBX);
        cubicY[2] = currentY + (arePixels ? controlBY * 20 : controlBY);
        cubicX[3] = currentX + (arePixels ? anchorX * 20 : anchorX);
        cubicY[3] = currentY + (arePixels ? anchorY * 20 : anchorY);
        
        flatten();
    }
    
    /**
     * Draw a quadratic bezier curve from the current point to the point (x,y) 
     * using the control point for the previously drawn curve.
     * 
     * If no curve has been drawn previously then a control point midway along 
     * the previous line or move is used.
     * 
     * @param xCoord the x-coordinate of the end of the curve.
     * @param yCoord the y-coordinate of the end of the curve.
     */
    public void reflect(int xCoord, int yCoord)
    {
        int rcontrolX = currentX - controlX;
        int rcontrolY = currentY - controlY;
        
        int pointX = (arePixels ? xCoord * 20 : xCoord) - currentX;
        int pointY = (arePixels ? yCoord * 20 : yCoord) - currentY;

        objects.add(new Curve(rcontrolX, rcontrolY, pointX, pointY));

        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
 
        setControl(rcontrolX+currentX, rcontrolY+currentY);
        setCurrent(pointX+currentX, pointY+currentY);
    }

    /**
     * Draw a quadratic bezier curve relative to the current point to the point 
     * using the control point for the previously drawn curve.
     * 
     * If no curve has been drawn previously then a control point midway along 
     * the previous line or move is used.
     * 
     * @param xCoord the distance along the x-axis from the current point to the end of the curve.
     * @param yCoord the distance along the y-axis from the current point to the end of the curve.
     */
    public void rreflect(int xCoord, int yCoord)
    {
        int rcontrolX = currentX - controlX;
        int rcontrolY = currentY - controlY;

        int pointX = arePixels ? xCoord * 20 : xCoord;
        int pointY = arePixels ? yCoord * 20 : yCoord;

        objects.add(new Curve(rcontrolX, rcontrolY, pointX, pointY));

        if (!pathInProgress)
        {
            setInitial(currentX, currentY);
            pathInProgress = true;
        }
 
        setControl(rcontrolX+currentX, rcontrolY+currentY);
        setCurrent(pointX+currentX, pointY+currentY);
    }

    /**
     * Draw a cubic bezier curve from the current point to the point (x,y). The 
     * first control point is the one defined for the previously drawn curve. 
     * The second control point is the coordinates (x2, y2).
     * 
     * If no curve has been drawn previously then a control point midway along 
     * the previous line or move is used.
     * 
     * @param ctrlX the x-coordinate of the control point.
     * @param ctrlY the y-coordinate of the control point.
     * @param anchorX the x-coordinate of the end of the curve.
     * @param anchorY the y-coordinate of the end of the curve.
     */
    public void reflect(int ctrlX, int ctrlY, int anchorX, int anchorY)
    {
        int acontrolX = currentX - controlX;
        int acontrolY = currentY - controlY;

        int bcontrolX = (arePixels ? ctrlX * 20 : ctrlX) - currentX;
        int bcontrolY = (arePixels ? ctrlY * 20 : ctrlY) - currentY;

        int pointX = (arePixels ? anchorX * 20 : anchorX) - currentX;
        int pointY = (arePixels ? anchorY * 20 : anchorY) - currentY;

        rcurve(acontrolX, acontrolY, bcontrolX, bcontrolY, pointX, pointY);
    }

    /**
     * Draw a cubic bezier curve relative to the current point. The first control 
     * point is the one defined for the previously drawn curve. The second control
     * point is the relative point (x2, y2).
     * 
     * If no curve has been drawn previously then a control point midway along 
     * the previous line or move is used.
     * 
     * @param ctrlX the distance along the x-axis from the current point to the second control point.
     * @param ctrlY the distance along the y-axis from the current point to the second control point.
     * @param anchorX the distance along the x-axis from the current point to the end of the curve.
     * @param anchorY the distance along the y-axis from the current point to the end of the curve.
     */
    public void rreflect(int ctrlX, int ctrlY, int anchorX, int anchorY)
    {
        int acontrolX = currentX - controlX;
        int acontrolY = currentY - controlY;

        int bcontrolX = arePixels ? ctrlX * 20 : ctrlX;
        int bcontrolY = arePixels ? ctrlY * 20 : ctrlY;

        int pointX = arePixels ? anchorX * 20 : anchorX;
        int pointY = arePixels ? anchorY * 20 : anchorY;

        rcurve(acontrolX, acontrolY, bcontrolX, bcontrolY, pointX, pointY);
    }

    /**
     * Draws a closed path in the shape of a rectangle with the specified width 
     * and height. The centre of the rectangle is located at the point (x,y).
     * 
     * The origin of the shape can be used to control the relative placement of 
     * the rectangle when it is placed on the Flash Player's display list using 
     * either the PlaceObject or PlaceObject2 class.
     * 
     * @param xCoord the x-coordinate of the centre of the rectangle.
     * @param yCoord the y-coordinate of the centre of the rectangle.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     */
    public void rect(int xCoord, int yCoord, int width, int height)
    {
        move(xCoord-width/2, yCoord-height/2);
        rline(width, 0);
        rline(0, height);
        rline(-width, 0);
        rline(0, -height);
    }

    /**
     * Draws a closed path in the shape of a rectangle with the specified width 
     * and height. The centre of the rectangle is located at the point (0,0).
     * 
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     */
    public void rect(int width, int height)
    {
        rect(0, 0, width, height);
    }

    /**
     * Draws a closed path in the shape of a rectangle with rounded corners. 
     * The shape is drawn with specified width and height and the radius argument 
     * specified the radius of the quarter circle used to draw the corners.
     * 
     * The centre of the rectangle is located at the point (x,y).
     * 
     * The origin of the shape can be used to control the relative placement of 
     * the rectangle when it is placed on the Flash Player's display list using 
     * either the PlaceObject or PlaceObject2 class.
     * 
     * @param xCoord the x-coordinate of the centre of the rectangle.
     * @param yCoord the y-coordinate of the centre of the rectangle.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param radius the radius of the quarter circle used to draw the corners.
     */
    public void rect(int xCoord, int yCoord, int width, int height, int radius)
    {
        int side = (height < width) ? height : width;
        int corner = (radius > side/2) ? side/2 : radius;
            
        move(xCoord, yCoord-height/2);
        rline(width/2-corner, 0);
        rcurve(corner, 0, 0, corner);
        rline(0, height - 2*corner);
        rcurve(0, corner, -corner, 0);
        rline(-(width-2*corner), 0);
        rcurve(-corner, 0, 0, -corner);
        rline(0, -(height-2*corner));
        rcurve(0, -corner, corner, 0);
        close();
    }

    /**
     * Draws a closed path in the shape of a rectangle with rounded corners. 
     * The shape is drawn with specified width and height and the radius argument 
     * specified the radius of the quarter circle used to draw the corners. The 
     * centre of the rectangle is located at the point (0,0).
     * 
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param radius the radius of the quarter circle used to draw the corners.
     */
    public void rect(int width, int height, int radius)
    {
        rect(0, 0, width, height, radius);
    }

    /**
     * Draws a closed path in the shape of an ellipse. The arguments rx and ry 
     * specify the radius of the ellipse in the x and y directions respectively.
     * 
     * The centre of the ellipse is located at the point (x,y).
     * 
     * The origin of the shape can be used to control the relative placement of 
     * the ellipse when it is placed on the Flash Player's display list using 
     * either the PlaceObject or PlaceObject2 class.
     * 
     * @param centreX the x-coordinate of the centre of the ellipse.
     * @param centreY the y-coordinate of the centre of the ellipse.
     * @param radiusX the radius of the ellipse in the x direction.
     * @param radiusY the radius of the ellipse in the y direction.
     */
    public void ellipse(int centreX, int centreY, int radiusX, int radiusY)
    {
        boolean wasInPixels = false;
        
        int pcx = centreX;
        int py = centreY;
        int prx = radiusX;
        int pry = radiusY;

        if (arePixels)
        {
            arePixels = false;
            wasInPixels = true;
            
            pcx *= 20;
            py *= 20;
            prx *= 20;
            pry *= 20;
        }

        int startX = (int) (0.707 * prx) + pcx;
        int startY = (int) (0.707 * pry) + py;

        int anchorX = (int) (0.293 * prx);
        int anchorY = (int) (0.293 * pry);
        int ctrlX = (int) (0.414 * prx);
        int ctrlY = (int) (0.414 * pry);
            
        move(startX, startY);
        rcurve(-anchorX, anchorY, -ctrlX, 0);
        rcurve(-ctrlX, 0, -anchorX, -anchorY);
        rcurve(-anchorX, -anchorY, 0, -ctrlY);
        rcurve(0, -ctrlY,  anchorX, -anchorY);
        rcurve(anchorX, -anchorY, ctrlX, 0);
        rcurve(ctrlX, 0,  anchorX, anchorY);
        rcurve(anchorX, anchorY, 0, ctrlY);
        rcurve(0, ctrlY, -anchorX, anchorY);
        
        if (wasInPixels)
        {
            arePixels = true;
        }
    }

    /**
     * Draws a closed path in the shape of an ellipse. The arguments rx and ry 
     * specify the radius of the ellipse in the x and y directions respectively.
     * 
     * The centre of the ellipse is located at the point (0,0).
     * 
     * @param radiusX the radius of the ellipse in the x direction.
     * @param radiusY the radius of the ellipse in the y direction.
     */
    public void ellipse(int radiusX, int radiusY)
    {
        ellipse(0, 0, radiusX, radiusY);
    }

    /**
     * Draws a closed path in the shape of a circle. The centre of the circle 
     * is located at the point (x,y) with radius r.
     * 
     * The origin of the shape can be used to control the relative placement of 
     * the circle when it is placed on the Flash Player's display list using 
     * either the PlaceObject or PlaceObject2 class.
     * 
     * @param xCoord the x-coordinate of the centre of the circle.
     * @param yCoord the y-coordinate of the centre of the circle.
     * @param radius the radius of the circle.
     */
    public void circle(int xCoord, int yCoord, int radius)
    {
        ellipse(xCoord, yCoord, radius, radius);
    }

    /**
     * Draws a closed path in the shape of a circle. The centre of the circle 
     * is located at the point (0,0) with radius r.
     * 
     * @param radius the radius of the circle.
     */
    public void circle(int radius)
    {
        ellipse(0, 0, radius, radius);
    }

    /**
     * Draws a closed shape with vertices defines by pairs of coordinates from 
     * the array argument. The first pair of points in the array specifies a move. 
     * Line segments a drawn relative to the current point which is updated after 
     * each segment is drawn.
     * 
     * If the number of points is an odd number then the last point will be ignored.
     * 
     * @param points and array of coordinate pairs. The first pair of points 
     * defines the coordinates of a move operation, successive pairs define the 
     * coordinates for relative lines.
     */
    public void rpolygon(int[] points)
    {
        int length = points.length;
        
        if (length % 2 == 1) {
            length -= 1;
        }
        
        rmove(points[0], points[1]);

        for (int i=2; i<length; i+=2) {
            rline(points[i], points[i+1]);
        }
            
        close();
    }

    /**
     * Draws a closed shape with vertices defines by pairs of coordinates from 
     * the array argument. The first pair of points in the array specifies a move. 
     * Line segments a drawn using absolute coordinates. The current point which 
     * is updated after each segment is drawn.
     * 
     * If the number of points is an odd number then the last point will be ignored.
     * 
     * @param points and array of coordinate pairs. The first pair of points defines the coordinates
     * of a move operation, successive pairs define the coordinates of the lines.
     */
    public void polygon(int[] points)
    {
        int length = points.length;
        
        if (length % 2 == 1) {
            length -= 1;
        }
        
        move(points[0], points[1]);

        for (int i=2; i<length; i+=2) {
            line(points[i], points[i+1]);
        }
            
        close();
    }

    private void setInitial(int xCoord, int yCoord)
    {
        initialX = xCoord;
        initialY = yCoord;
    }
    
    private void setCurrent(int xCoord, int yCoord)
    {
        currentX = xCoord;
        currentY = yCoord;
        
        if ((xCoord - lineWidth/2) < minX) {
        	minX = xCoord - lineWidth/2;
        }
        if ((yCoord - lineWidth/2) < minY) {
        	minY = yCoord - lineWidth/2;
        }
        if ((xCoord + lineWidth/2) > maxX) {
        	maxX = xCoord + lineWidth/2;
        }
        if ((yCoord + lineWidth/2) > maxY) {
        	maxY = yCoord + lineWidth/2;
        }
    }
    
    private void setControl(int xCoord, int yCoord)
    {
        controlX = xCoord;
        controlY = yCoord;

        if ((xCoord - lineWidth/2) < minX) {
        	minX = xCoord - lineWidth/2;
        }
        if ((yCoord - lineWidth/2) < minY) {
        	minY = yCoord - lineWidth/2;
        }
        if ((xCoord + lineWidth/2) > maxX) {
        	maxX = xCoord + lineWidth/2;
        }
        if ((yCoord + lineWidth/2) > maxY) {
        	maxY = yCoord + lineWidth/2;
        }
    }
    
    private void setBounds(int xmin, int ymin, int xmax, int ymax)
    {
        minX = xmin;
        minY = ymin;
        maxX = xmax;
        maxY = ymax;
    }
    
    private void flatten()
    {
        double[] quadX = new double[] {0.0, 0.0, 0.0, 0.0};
        double[] quadY = new double[] {0.0, 0.0, 0.0, 0.0};

        double delta;
        double pointAX;
        double pointAY;
        double pointBX;
        double pointBY;

        while (true) 
        {
            pointAX = 2.0 * cubicX[0] + cubicX[3] - 3.0 * cubicX[1];  pointAX *= pointAX;
            pointBX = 2.0 * cubicX[3] + cubicX[0] - 3.0 * cubicX[2];  pointBX *= pointBX;
        
            if (pointAX < pointBX) {
            	pointAX = pointBX;
            }

            pointAY = 2.0 * cubicY[0] + cubicY[3] - 3.0 * cubicY[1];  pointAY *= pointAY;
            pointBY = 2.0 * cubicY[3] + cubicY[0] - 3.0 * cubicY[2];  pointBY*= pointBY;
        
            if (pointAY < pointBY) {
            	pointAY = pointBY;
            }

            if ((pointAX + pointAY) < FLATTEN_LIMIT) 
            {
                objects.add(new Line((int)(cubicX[3])-currentX, (int)(cubicY[3])-currentY));
                setControl((int)(cubicX[1]), (int)(cubicY[1]));
                setControl((int)(cubicX[2]), (int)(cubicY[2]));
                setCurrent((int)(cubicX[3]), (int)(cubicY[3]));
                break;
            }
            else 
            {
                quadX[3] = cubicX[3];
                delta = (cubicX[1] + cubicX[2]) / 2;
                cubicX[1] = (cubicX[0] + cubicX[1]) / 2;
                quadX[2] = (cubicX[2] + cubicX[3]) / 2;
                cubicX[2] = (cubicX[1] + delta) / 2;
                quadX[1] = (delta + quadX[2]) / 2;
                cubicX[3] = (cubicX[2] + quadX[1]) / 2;
                quadX[0] = (cubicX[2] + quadX[1]) / 2;

                quadY[3] = cubicY[3];
                delta = (cubicY[1] + cubicY[2]) / 2;
                cubicY[1] = (cubicY[0] + cubicY[1]) / 2;
                quadY[2] = (cubicY[2] + cubicY[3]) / 2;
                cubicY[2] = (cubicY[1] + delta) / 2;
                quadY[1] = (delta + quadY[2]) / 2;
                cubicY[3] = (cubicY[2] + quadY[1]) / 2;
                quadY[0] = (cubicY[2] + quadY[1]) / 2;

                flatten();
                  
                cubicX[0] = quadX[0];
                cubicY[0] = quadY[0];
                cubicX[1] = quadX[1];
                cubicY[1] = quadY[1];
                cubicX[2] = quadX[2];
                cubicY[2] = quadY[2];
                cubicX[3] = quadX[3];
                cubicY[3] = quadY[3];
            }
        }
    }
}

