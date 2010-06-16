/*
 * ImageFactory.java
 * Transform
 *
 * Copyright (c) 2009-2010 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.util.image;

import java.io.File;
import java.io.FileInputStream;
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

import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.CoordTransform;
import com.flagstone.transform.fillstyle.BitmapFill;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.linestyle.LineStyle;
import com.flagstone.transform.linestyle.LineStyle1;
import com.flagstone.transform.shape.DefineShape3;
import com.flagstone.transform.shape.Line;
import com.flagstone.transform.shape.Shape;
import com.flagstone.transform.shape.ShapeRecord;
import com.flagstone.transform.shape.ShapeStyle;
import com.flagstone.transform.video.ImageBlock;

/**
 * <p>
 * ImageFactory is used to generate an image definition object from an image
 * stored in a file, references by a URL or read from an stream. An plug-in
 * architecture allows decoders to be registered to handle different image
 * formats. The ImageFactory provides a standard interface for using the
 * decoders.
 * </p>
 *
 * <p>
 * Currently PNG, BMP and JPEG encoded images are supported by dedicated
 * decoders. The BufferedImageDecoder can be used to decode any format supported
 * using Java's ImageIO, including PNG, BMP and JPG format images. New decoders
 * can be added by implementing the ImageDecoder interface and registering them
 * in the ImageRegistry.
 * </p>
 *
 * <P>
 * The defineImage() methods return an Definition (the abstract base class for
 * all objects used to define shapes etc. in a Flash file. The exact class of
 * the object generated depends of the format of the image loaded.
 * </P>
 *
 * <table>
 * <tr>
 * <th>Class</th>
 * <th>Generated when...</th>
 * </tr>
 *
 * <tr>
 * <td valign="top">DefineJPEGImage2</td>
 * <td>A JPEG encoded image is loaded. The getFormat() method returns the class
 * constant JPEG.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">DefineImage</td>
 * <td>An indexed BMP or PNG image contains a colour table without transparent
 * colours or when a true colour image contains 16-bit or 24-bit colours is
 * loaded. The getFormat() method returns the class constants IDX8, RGB5 or
 * RGB8.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">DefineImage2</td>
 * <td>A BMP or PNG indexed image contains a colour table with transparent
 * colours is loaded or when a true colour image contains 32-bit bit colours.
 * The getFormat() method returns the class constants IDXA or RGBA.</td>
 * </tr>
 *
 * </table>
 *
 * <P>
 * Images are displayed in Flash by filling a shape with the image bitmap. The
 * defineEnclosingShape() method generates a rectangular shape object which
 * wraps the image:
 *
 * <pre>
 *     int imageId = movie.newIdentifier();
 *     int shapeId = movie.newIdentifier();
 *
 *     Definition image = Image(defineImage(imageId, ...);
 *
 *     int x = image.getWidth()/2;
 *     int y = image.getHeight()/2;
 *
 *     LineStyle style = new LineStyle(20, ColorTable.black());
 *
 *     movie.add(image);
 *     movie.add(Image.defineEnclosingShape(shapeId, image, x, y, style);
 * </pre>
 *
 * <P>
 * Here the origin, used when placing the shape on the screen, is defined as the
 * centre of the shape. Other points may be defined to suit the alignment of the
 * shape when it is placed on the display list.
 * </P>
 */
public final class ImageFactory {

    private static final int TWIPS_PER_PIXEL = 20;

    private static final int RGBA_CHANNELS = 4;
    private static final int RGB_CHANNELS = 3;

    /** The object used to decode the image. */
    private transient ImageDecoder decoder;

    /**
     * Create an image definition for the image located in the specified file.
     *
     * @param file
     *            a file containing the abstract path to the image.
     *
     * @throws IOException
     *             if there is an error reading the file.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image, either it is in an
     *             unsupported format or an error occurred while decoding the
     *             image data.
     */
    public void read(final File file) throws IOException, DataFormatException {

        final ImageInfo info = new ImageInfo();
        info.setInput(new RandomAccessFile(file, "r"));
//        info.setDetermineImageNumber(true);

        if (!info.check()) {
            throw new DataFormatException("Unsupported format");
        }

        decoder = ImageRegistry.getImageProvider(
                info.getImageFormat().getMimeType());
        decoder.read(new FileInputStream(file));
    }

    /**
     * Create an image definition for the image referenced by a URL.
     *
     * @param url
     *            the Uniform Resource Locator referencing the file.
     *
     * @throws IOException
     *             if there is an error reading the file.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image, either it is in an
     *             unsupported format or an error occurred while decoding the
     *             image data.
     */
    public void read(final URL url) throws IOException, DataFormatException {

        final URLConnection connection = url.openConnection();
        final int fileSize = connection.getContentLength();

        if (fileSize < 0) {
            throw new FileNotFoundException(url.getFile());
        }

        final String mimeType = connection.getContentType();
        decoder = ImageRegistry.getImageProvider(mimeType);

        if (decoder == null) {
            throw new DataFormatException("Unsupported format");
        }

        decoder.read(url.openStream());
    }

    /**
     * Create an image definition for an image read from a stream.
     *
     * @param stream
     *            the InputStream containing the image data.
     *
     * @throws IOException
     *             if there is an error reading the stream.
     *
     * @throws DataFormatException
     *             if there is a problem decoding the image, either it is in an
     *             unsupported format or an error occurred while decoding the
     *             image data.
     */
    public void read(final InputStream stream)
            throws IOException, DataFormatException {
        decoder.read(stream);
    }

    /**
     * Create a definition for the image so it can be added to a Flash movie.
     * @param identifier the unique identifier for the image.
     * @return an ImageTag representing one of the image definitions supported
     * in Flash.
     */
    public ImageTag defineImage(final int identifier) {
        return decoder.defineImage(identifier);
    }

    /**
     * Generates the shape definition object that is required to display an
     * image in a Flash movie. The shape is generated with a single fill style
     * (BitmapFill object). The origin of the shape is specified relative to the
     * top left corner of the image.
     *
     * The borderStyle argument specifies a border that will be drawn around the
     * image. The style may be set to null is no border is drawn.
     *
     * @param shapeId
     *            an unique identifier that is used to reference the shape
     *            definition in a Flash movie.
     *
     * @param imageId
     *            the unique identifier of the image generated using the
     *            defineImage() method.
     *
     * @param xOrigin
     *            the offset in pixels along the x-axis, relative to the top
     *            left corner of the image, where the origin (0,0) of the shape
     *            will be located.
     *
     * @param yOrigin
     *            the offset in pixels along the y-axis, relative to the top
     *            left corner of the image, where the origin (0,0) of the shape
     *            will be located.
     *
     * @param borderStyle
     *            the style drawn around the border of the image. May be null if
     *            no border is drawn.
     *
     * @return the shape that is used to display the image in a Flash movie.
     */
    public DefineShape3 defineEnclosingShape(final int shapeId,
            final int imageId, final int xOrigin, final int yOrigin,
            final LineStyle1 borderStyle) {

        int lineWidth = 0;

        if (borderStyle != null) {
            lineWidth = borderStyle.getWidth() / 2;
        }

        final Bounds bounds = new Bounds(
                -xOrigin * TWIPS_PER_PIXEL - lineWidth,
                -yOrigin * TWIPS_PER_PIXEL - lineWidth,
                (decoder.getWidth() - xOrigin) * TWIPS_PER_PIXEL + lineWidth,
                (decoder.getHeight() - yOrigin) * TWIPS_PER_PIXEL + lineWidth);

        final Shape shape = new Shape(new ArrayList<ShapeRecord>());
        final ShapeStyle style = new ShapeStyle().setLineStyle(
                borderStyle == null ? 0 : 1).setFillStyle(1);
        style.setMove(-xOrigin * TWIPS_PER_PIXEL, -yOrigin * TWIPS_PER_PIXEL);

        shape.add(style);
        shape.add(new Line(decoder.getWidth() * TWIPS_PER_PIXEL, 0));
        shape.add(new Line(0, decoder.getHeight() * TWIPS_PER_PIXEL));
        shape.add(new Line(-decoder.getWidth() * TWIPS_PER_PIXEL, 0));
        shape.add(new Line(0, -decoder.getHeight() * TWIPS_PER_PIXEL));

        final DefineShape3 definition = new DefineShape3(shapeId, bounds,
                new ArrayList<FillStyle>(), new ArrayList<LineStyle>(), shape);
        final CoordTransform transform = new CoordTransform(
                TWIPS_PER_PIXEL, TWIPS_PER_PIXEL, 0, 0,
                -xOrigin * TWIPS_PER_PIXEL, -yOrigin * TWIPS_PER_PIXEL);

        if (borderStyle != null) {
            definition.add(borderStyle);
        }

        definition.add(new BitmapFill(false, false, imageId, transform));

        return definition;
    }

    /**
     * Return an image stored in a a file as a list of ImageBlock objects that
     * can be used when creating ScreenVideo streams.
     *
     * The image is divided by tiling blocks of the specified width and height
     * across the image. For blocks at the right and bottom edges the size of
     * the block may be reduced so that it fits the image exactly. In other
     * words the blocks are not padded with extra pixel information.
     *
     * @param  blocks
     *            a list of FMImageBlock objects.
     * @param blockWidth
     *            the width of a block in pixels.
     * @param blockHeight
     *            the height of a block in pixels
     */
    public void getImageAsBlocks(final List<ImageBlock> blocks,
            final int blockWidth, final int blockHeight) {
        int row = 0;
        int col = 0;

        int src = 0;
        int dst = 0;

        final byte[] image = decoder.getImage();
        final int width = decoder.getWidth();
        final int height = decoder.getHeight();

        final byte[] formattedImage = new byte[width * height * RGB_CHANNELS];

        for (row = height - 1; row >= 0; row--) {
            src = row * width;

            for (col = 0; col < width; col++, src += RGBA_CHANNELS) {
                formattedImage[dst++] = image[src + 2];
                formattedImage[dst++] = image[src + 1];
                formattedImage[dst++] = image[src];
            }
        }

        final int columns = (width + blockWidth - 1) / blockWidth;
        final int rows = (height + blockHeight - 1) / blockHeight;

        final byte[] blockData = new byte[blockHeight * blockWidth
                                          * RGB_CHANNELS];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                final int xOffset = j * blockWidth;
                final int yOffset = i * blockHeight;

                final int xSpan = (width - xOffset > blockWidth) ? blockWidth
                        : width - xOffset;
                final int ySpan = (height - yOffset > blockHeight) ? blockHeight
                        : height - yOffset;
                int offset = 0;

                int idx;

                for (int k = 0; k < ySpan; k++) {
                    for (int l = 0; l < xSpan; l++, offset += RGB_CHANNELS) {
                        idx = (yOffset + k) * (width * RGB_CHANNELS)
                                + (xOffset + l) * RGB_CHANNELS;

                        blockData[offset] = formattedImage[idx];
                        blockData[offset + 1] = formattedImage[idx + 1];
                        blockData[offset + 2] = formattedImage[idx + 2];
                    }
                }

                blocks.add(new ImageBlock(xSpan, ySpan,
                        zip(blockData, offset)));
            }
        }
    }

    /**
     * Compress the image using the ZIP format.
     * @param image the image data.
     * @param length the number of bytes from the image to compress.
     * @return the compressed image.
     */
    private byte[] zip(final byte[] image, final int length) {
        final Deflater deflater = new Deflater();
        deflater.setInput(image, 0, length);
        deflater.finish();

        final byte[] compressedData = new byte[image.length];
        final int bytesCompressed = deflater.deflate(compressedData);
        final byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);

        return newData;
    }
}
