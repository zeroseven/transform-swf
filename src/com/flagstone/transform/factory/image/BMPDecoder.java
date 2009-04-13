/*
 *  ImageConstructor.java
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

package com.flagstone.transform.factory.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import com.flagstone.transform.coder.LittleEndianDecoder;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.image.DefineImage;
import com.flagstone.transform.movie.image.DefineImage2;

/**
 * BMPDecoder decodes Bitmap images (BMP) so they can be used in a Flash file.
 */
public final class BMPDecoder implements ImageProvider, ImageDecoder
{
    protected final static int[] bmpSignature = { 66, 77 };

    protected final static int BI_RGB = 0;
    protected final static int BI_RLE8 = 1;
    protected final static int BI_RLE4 = 2;
    protected final static int BI_BITFIELDS = 3;
    
    private ImageEncoding format;
    private int width;
    private int height;
    private byte[] table;
    private byte[] image;

    private int bitDepth;
    private int compressionMethod;
    private int redMask;
    private int greenMask;
    private int blueMask;
     
    public void read(String path) throws FileNotFoundException, IOException, DataFormatException
    {
    	read(new File(path));
    }
    
    public void read(File file) throws FileNotFoundException, IOException, DataFormatException
    {
    	ImageInfo info = new ImageInfo();
    	info.setInput(new RandomAccessFile(file, "r"));
    	info.setDetermineImageNumber(true);
    	
    	if (!info.check()) 
    	{
    		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
    	}
    	
		decode(loadFile(file));
    }

    public void read(URL url) throws FileNotFoundException, IOException, DataFormatException
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
    	
		decode(bytes);
    }

    public ImageTag defineImage(int identifier)
    {
    	ImageTag object = null;
    	
		switch (format)
        {
            case IDX8: 
            	object = new DefineImage(identifier, width, height, table.length, zip(merge(adjustScan(width, height, image), table))); 
            	break;
            case IDXA: 
            	object = new DefineImage2(identifier, width, height, table.length, zip(mergeAlpha(adjustScan(width, height, image), table))); 
            	break;
            case RGB5: 
            	object = new DefineImage(identifier, width, height, zip(packColours(width, height, image)), 16); 
            	break;
            case RGB8: 
            	orderAlpha(image);
            	object = new DefineImage(identifier, width, height, zip(image), 24); 
            	break;
            case RGBA: 
            	applyAlpha(image);
            	object = new DefineImage2(identifier, width, height, zip(image)); 
            	break;
            default:
            	break; //TODO fix this 
        }
    	return object;
    }

    public ImageDecoder newDecoder() {
    	return new BMPDecoder();
    }
    
    public int getWidth() {
    	return width;
    }
    public int getHeight() {
    	return height;
    }
    
    public byte[] getImage() {
    	return Arrays.copyOf(image, image.length);
    }
    
	protected void decode(byte[] bytes) throws DataFormatException
    {
        LittleEndianDecoder coder = new LittleEndianDecoder(bytes);
        
        for (int i=0; i<2; i++)
        {
            if (coder.readByte() != bmpSignature[i]) {
                throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }

        coder.readWord(4, false); // fileSize
        coder.readWord(4, false); // reserved
        
        int offset = coder.readWord(4, false);
        int headerSize = coder.readWord(4, false);
        
        int bitsPerPixel;
        int coloursUsed;

        switch (headerSize)
        {
            case 12:
                width = coder.readWord(2, false);
                height = coder.readWord(2, false);
                coder.readWord(2, false); // bitPlanes
                bitsPerPixel = coder.readWord(2, false);
                coloursUsed = 0;
                break;
            case 40:
                width = coder.readWord(4, false);
                height = coder.readWord(4, false);
                coder.readWord(2, false); // bitPlanes
                bitsPerPixel = coder.readWord(2, false);
                compressionMethod = coder.readWord(4, false);
                coder.readWord(4, false); //imageSize
                coder.readWord(4, false); // horizontalResolution
                coder.readWord(4, false); // verticalResolution
                coloursUsed = coder.readWord(4, false);
                coder.readWord(4, false); // importantColours
                break;
            default:
            	bitsPerPixel = 0;
            	coloursUsed = 0;
                break;
        }
        
        if (compressionMethod == BI_BITFIELDS)
        {
            redMask = coder.readWord(4, false);
            greenMask = coder.readWord(4, false);
            blueMask = coder.readWord(4, false);
        }
        
        switch (bitsPerPixel)
        {
            case 1: format = ImageEncoding.IDX8; bitDepth = 1; break;
            case 2: format = ImageEncoding.IDX8; bitDepth = 2; break;
            case 4: format = ImageEncoding.IDX8; bitDepth = 4; break;
            case 8: format = ImageEncoding.IDX8; bitDepth = 8; break;
            case 16: format = ImageEncoding.RGB5; bitDepth = 5; break;
            case 24: format = ImageEncoding.RGB8; bitDepth = 8; break;
            case 32: format = ImageEncoding.RGBA; bitDepth = 8; break;
            default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }
        
        if (format == ImageEncoding.IDX8) 
        {
            coloursUsed = 1 << bitsPerPixel;
            table = new byte[coloursUsed*4];
            image = new byte[height*width];
            
            int index = 0;

            if (headerSize == 12)
            {
                for (int i=0; i<coloursUsed; i++, index+=4) 
                {
                    table[index+3] = (byte)0xFF;
                    table[index+2] = (byte)coder.readByte();
                    table[index+1] = (byte)coder.readByte();
                    table[index] = (byte)coder.readByte();
                }
            }
            else
            {
                for (int i=0; i < coloursUsed; i++, index+=4)
                {
                    table[index] = (byte)coder.readByte();
                    table[index+1] = (byte)coder.readByte();
                    table[index+2] = (byte)coder.readByte();
                    table[index+3] = (byte)(coder.readByte() | 0xFF);
                }
            }
                
            coder.setPointer(offset<<3);

            switch (compressionMethod)
            {
                case BI_RGB:  decodeIDX8(coder); break;
                case BI_RLE8: decodeRLE8(coder); break;
                case BI_RLE4: decodeRLE4(coder); break;
                default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }
        else
        {
            image = new byte[height*width*4];

            coder.setPointer(offset<<3);

            switch (format)
            {
                case RGB5: decodeRGB5(coder); break;
                case RGB8: decodeRGB8(coder); break;
                case RGBA: decodeRGBA(coder); break;
                default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }
    }

    private void decodeIDX8(LittleEndianDecoder coder)
    {
        int bitsRead;
        int index = 0;
        
        for (int row=height-1; row>0; row--)
        {
        	bitsRead=0;
        	 
            for (int col=0; col<width; col++)
            {
                image[index++] = (byte)coder.readBits(bitDepth, false);
                bitsRead += bitDepth;
            }
            
            if (bitsRead % 32 > 0) {
                coder.adjustPointer(32 - (bitsRead % 32));
            }
        }
    }

    private void decodeRLE4(LittleEndianDecoder coder)
    {
        int row = height-1;
        int col = 0;
        int index = 0;
        
        boolean hasMore = true;

        while (hasMore) 
        {       
            int count = coder.readByte();
        
            if (count == 0)
            {
                int code = coder.readByte();
                
                switch (code)
                {
                    case 0: 
                        col = 0; 
                        row--; 
                        break;
                    case 1: 
                        hasMore = false; 
                        break;
                    case 2: 
                        col += coder.readWord(2, false);
                        row -= coder.readWord(2, false);
                        for (int i=0; i<code; i+=2)
                        {
                            image[index++] = (byte) coder.readBits(4, false);
                            image[index++] = (byte) coder.readBits(4, false);
                        }
                        
                        if ((code & 2) == 2) {
                        	coder.readByte();
                        }
                        break;
                    default:
                        for (int i=0; i<code; i+=2)
                        {
                            image[index++] = (byte) coder.readBits(4, false);
                            image[index++] = (byte) coder.readBits(4, false);
                        }
                        
                        if ((code & 2) == 2) {
                        	coder.readByte();
                        }
                        break;
                }
            }
            else
            {
                byte indexA = (byte)coder.readBits(4, false);
                byte indexB = (byte)coder.readBits(4, false);
                
                for (int i=0; i<count && col < width; i++) { 
                    image[index++] = (i % 2 > 0) ? indexB : indexA;
                }
            }
        }
    }
    
    private void decodeRLE8(LittleEndianDecoder coder)
    {
        int row = height-1;
        int col = 0;
        int index = 0;
        
        boolean hasMore = true;

        while (hasMore) 
        {       
            int count = coder.readByte();
        
            if (count == 0)
            {
                int code = coder.readByte();
                
                switch (code)
                {
                    case 0: 
                        col = 0; 
                        row--; 
                        break;
                    case 1: 
                        hasMore = false; 
                        break;
                    case 2: 
                        col += coder.readWord(2, false);
                        row -= coder.readWord(2, false);
                        for (int i=0; i<code; i++) {
                            image[index++] = (byte) coder.readByte();
                        }
                        
                        if ((code & 1) == 1) {
                        	coder.readByte();
                        }
                        break;
                    default:
                        for (int i=0; i<code; i++) {
                            image[index++] = (byte) coder.readByte();
                        }
                        
                        if ((code & 1) == 1) {
                        	coder.readByte();
                        }
                        break;
                }
            }
            else
            {
                byte value = (byte)coder.readByte();
                
                for (int i=0; i<count; i++) { 
                    image[index++] = value;
                }
            }
        }
    }
    
    private void decodeRGB5(LittleEndianDecoder coder)
    {
        int bitsRead = 0;
        int index = 0;
        
        if (compressionMethod == BI_RGB)
        {
            for (int row=height-1; row>0; row--)
            {
            	bitsRead=0;
            	
                for (int col=0; col<width; col++, index+=4)
                {                
                    int colour = coder.readWord(2, false) & 0xFFFF;
                    
                    image[index] = (byte)((colour & 0x7C00) >> 7);
                    image[index+1] = (byte)((colour & 0x03E0) >> 2);
                    image[index+2] = (byte)((colour & 0x001F) << 3);
                    image[index+3] = (byte)0xFF;

                    bitsRead += 16;
                }
                if (bitsRead % 32 > 0) {
                    coder.adjustPointer(32 - (bitsRead % 32));
                }
            }
        }
        else
        {
            for (int row=height-1; row>0; row--)
            {
            	bitsRead=0;
            	
                for (int col=0; col<width; col++, index+=4)
                {
                    int colour = coder.readWord(2, false) & 0xFFFF;
                    
                    if (redMask == 0x7C00 && greenMask == 0x03E0 && blueMask == 0x001F)
                    {
                        image[index] = (byte)((colour & 0x7C00) >> 7);
                        image[index+1] = (byte)((colour & 0x03E0) >> 2);
                        image[index+2] = (byte)((colour & 0x001F) << 3);
                        image[index+3] = (byte)0xFF;
                    }
                    else if (redMask == 0xF800 && greenMask == 0x07E0 && blueMask == 0x001F)
                    {
                        image[index] = (byte)((colour & 0xF800) >> 8);
                        image[index+1] = (byte)((colour & 0x07E0) >> 3);
                        image[index+2] = (byte)((colour & 0x001F) << 3);
                        image[index+3] = (byte)0xFF;
                    }
                    bitsRead += 16;
                }
                if (bitsRead % 32 > 0) {
                    coder.adjustPointer(32 - (bitsRead % 32));
                }
            }
        }
        
    }

    private void decodeRGB8(LittleEndianDecoder coder)
    {
        int bitsRead;
        int index = 0;
        
        for (int row=height-1; row>0; row--)
        {
        	bitsRead=0;
        	
            for (int col=0; col<width; col++, index+=4)
            {
                image[index] = (byte)coder.readBits(bitDepth, false);
                image[index+1] = (byte)coder.readBits(bitDepth, false);
                image[index+2] = (byte)coder.readBits(bitDepth, false);
                image[index+3] = (byte)0xFF;
                
                bitsRead += 24;
            }
            
            if (bitsRead % 32 > 0) {
                coder.adjustPointer(32 - (bitsRead % 32));
            }
        }
    }

    private void decodeRGBA(LittleEndianDecoder coder)
    {
        int index = 0;
        
        for (int row=height-1; row>0; row--)
        {
            for (int col=0; col<width; col++, index+=4)
            {
                image[index+2] = (byte)coder.readByte();
                image[index+1] = (byte)coder.readByte();
                image[index] = (byte)coder.readByte();
                image[index+3] = (byte)coder.readByte();
                image[index+3] = (byte)0xFF;
            }
        }
    }
    
    private void orderAlpha(byte[] image)
    {
    	byte alpha;
    	
        for (int i=0; i<image.length; i+=4)
        {
        	alpha = image[i+3];
        	
        	image[i+3] = image[i+2];
        	image[i+2] = image[i+1];
        	image[i+1] = image[i];
        	image[i] = alpha;
        }
    }

    private void applyAlpha(byte[] image)
    {
    	int alpha; 
    	
    	for (int i=0; i<image.length; i+=4)
    	{
            alpha = image[i+3] & 0xFF;                

            image[i] =   (byte)(((image[i]   & 0xFF) * alpha) / 255);
            image[i+1] = (byte)(((image[i+1] & 0xFF) * alpha) / 255);
            image[i+2] = (byte)(((image[i+2] & 0xFf) * alpha) / 255);
    	}
    }
    
    private byte[] merge(byte[] image, byte[] table)
    {
        byte[] merged = new byte[(table.length/4)*3+image.length];
        int dst = 0;
        
        for (int i=0; i<table.length; i+=4)
        {
            merged[dst++] = table[i]; // R
            merged[dst++] = table[i+1]; // G
            merged[dst++] = table[i+2]; // B
        }
        
        for (int i=0; i<image.length; i++) {
            merged[dst++] = image[i];
        }

        return merged;
    }
  
    private byte[] mergeAlpha(byte[] image, byte[] table)
    {
        byte[] merged = new byte[table.length+image.length];
        int dst = 0;
        
        for (int i=0; i<table.length; i++) {
            merged[dst++] = table[i];
        }
        
        for (int i=0; i<image.length; i++) {
            merged[dst++] = image[i];
        }
        return merged;
    }

    private byte[] zip(byte[] image)
    {
        Deflater deflater = new Deflater();
        deflater.setInput(image);
        deflater.finish();
        
        byte[] compressedData = new byte[image.length*2];
        int bytesCompressed = deflater.deflate(compressedData);
        byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);
            
        return newData;
    }

	private byte[] loadFile(final File file) throws FileNotFoundException, IOException {
		byte[] data = new byte[(int) file.length()];

		FileInputStream stream = null; // NOPMD

		try {
			stream = new FileInputStream(file);
			int bytesRead = stream.read(data);

			if (bytesRead != data.length) {
				throw new IOException(file.getAbsolutePath());
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return data;
	}
    
    private  byte[] adjustScan(int width, int height, byte[] image)
    {
        int src = 0;
        int dst = 0;
        int row;
        int col;
        
        int scan = 0;
        byte[] formattedImage = null;
        
        scan = (width + 3) & ~3;
        formattedImage = new byte[scan*height];
        
        for (row=0; row<height; row++)
        {
            for (col=0; col<width; col++) {
                formattedImage[dst++] = image[src++];
            }
            
            while (col++ < scan) {
                formattedImage[dst++] = 0;
            }
        }

        return formattedImage;
    }
    
    private byte[] packColours(int width, int height, byte[] image)
    {
        int src = 0;
        int dst = 0;
        int row;
        int col;
        
        int scan = width + (width & 1);
        byte[] formattedImage = new byte[scan*height*2];
        
        for (row=0; row<height; row++)
        {
            for (col=0; col<width; col++, src++)
            {
                int red = (image[src++] & 0xF8) << 7;
                int green = (image[src++] & 0xF8) << 2;
                int blue = (image[src++] & 0xF8) >> 3;
                int colour = (red | green | blue) & 0x7FFF;
                
                formattedImage[dst++] = (byte) (colour >> 8);
                formattedImage[dst++] = (byte) colour;
            }

            while (col<scan)
            {
                formattedImage[dst++] = 0;
                formattedImage[dst++] = 0;
                col++;
            }
        }
        return formattedImage;
    }
    

}
