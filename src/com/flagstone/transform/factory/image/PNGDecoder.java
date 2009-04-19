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
import java.util.zip.Inflater;

import com.flagstone.transform.coder.BigEndianDecoder;
import com.flagstone.transform.movie.ImageTag;
import com.flagstone.transform.movie.Strings;
import com.flagstone.transform.movie.image.DefineImage;
import com.flagstone.transform.movie.image.DefineImage2;

/**
 * PNGDecoder decodes Portable Network Graphics (PNG) format images so they can 
 * be used in a Flash file.
 */
public final class PNGDecoder implements ImageProvider, ImageDecoder
{
    // Tables mapping grey scale values onto 8-bit colour channels
    
    protected static final int[] MONOCHROME = { 0, 255 };
    protected static final int[] GREYCSALE2 = { 0, 85, 170, 255 };
    protected static final int[] GREYCSALE4 = { 
    	0, 17, 34, 51, 
    	68, 85, 102, 119, 
    	136, 153, 170, 187, 
    	204, 221, 238, 255 };
    
    // Constants used for PNG images 
        
    protected static final int[] SIGNATURE = { 137, 80, 78, 71, 13, 10, 26, 10 };

    protected static final int CRITICAL_CHUNK = 0x20000000;
    
    protected static final int IHDR = 0x49484452;
    protected static final int PLTE = 0x504c5445;
    protected static final int IDAT = 0x49444154;
    protected static final int IEND = 0x49454e44;
    protected static final int TRNS = 0x74524e53;
    protected static final int BKGD = 0x624b4744;
    protected static final int CHRM = 0x6348524d;
    protected static final int FRAC = 0x66524163;
    protected static final int GAMA = 0x67414d41;
    protected static final int GIFG = 0x67494667;
    protected static final int GIFT = 0x67494674;
    protected static final int GIFX = 0x67494678;
    protected static final int HIST = 0x68495354;
    protected static final int ICCP = 0x69434350;
    protected static final int ITXT = 0x69545874;
    protected static final int OFFS = 0x6f464673;
    protected static final int PCAL = 0x7043414c;
    protected static final int PHYS = 0x70485973;
    protected static final int SBIT = 0x73424954;
    protected static final int SCAL = 0x7343414c;
    protected static final int SPLT = 0x73504c54;
    protected static final int SRGB = 0x73524742;
    protected static final int TEXT = 0x74455874;
    protected static final int TIME = 0x74494d45;
    protected static final int ZTXT = 0x7a545874;
    
    protected static final int GREYSCALE = 0;
    protected static final int TRUE_COLOUR = 2;
    protected static final int INDEXED_COLOUR = 3;
    protected static final int ALPHA_GREYSCALE = 4;
    protected static final int ALPHA_TRUECOLOUR = 6;
    
    protected static final int NO_FILTER = 0;
    protected static final int SUB_FILTER = 1;
    protected static final int UP_FILTER = 2;
    protected static final int AVG_FILTER = 3;
    protected static final int PAETH_FILTER = 4;
    
    protected static final int[] startRow =        { 0, 0, 4, 0, 2, 0, 1 };
    protected static final int[] startColumn =     { 0, 4, 0, 2, 0, 1, 0 };
    protected static final int[] rowIncrement =    { 8, 8, 8, 4, 4, 2, 2 };
    protected static final int[] columnIncrement = { 8, 8, 4, 4, 2, 2, 1 };
    
    protected static final int BIT_DEPTH = 0;
    protected static final int COLOUR_COMPONENTS = 1;
    protected static final int COMPRESSION = 2;
    
    protected static final int COLOUR_TYPE = 4;
    protected static final int FILTER_METHOD = 5;
    protected static final int INTERLACE_METHOD = 6;
    protected static final int TRANSPARENT_GREY = 7;
    protected static final int TRANSPARENT_RED = 8;
    protected static final int TRANSPARENT_GREEN = 9;
    protected static final int TRANSPARENT_BLUE = 10;
    
    private int[] attributes = new int[16];
    private byte[] chunkData = new byte[0];
    
    private ImageFormat format;
    private int width;
    private int height;
    private byte[] table;
    private byte[] image;

    public ImageDecoder newDecoder() {
    	return new PNGDecoder();
    }
    
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
            	break; //TODO Fix this
        }
    	return object;
    }

    protected void decode(byte[] bytes) throws DataFormatException
    {
        BigEndianDecoder coder = new BigEndianDecoder(bytes);

        int length = 0;
        int chunkType = 0;
        boolean moreChunks = true;

        for (int i=0; i<8; i++)
        {
            if (coder.readByte() != SIGNATURE[i]) {
                throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
            }
        }
        
        while (moreChunks)
        {
            length = coder.readWord(4, false);
            chunkType = coder.readWord(4, false);
            
            int current = coder.getPointer();
            int next = current + ((length+4) << 3);
            
            switch (chunkType)
            {
                case IHDR: decodeIHDR(coder); break;
                case PLTE: decodePLTE(coder, length); break;
                case TRNS: decodeTRNS(coder, length); break;
                case IDAT: decodeIDAT(coder, length); break;
                case IEND: moreChunks = false; coder.adjustPointer(32); break;
                default:
                    coder.adjustPointer((length+4) << 3);
                    break;
            }         
            length += 4; // include CRC at end of chunk
            coder.setPointer(next);
            
            if (coder.eof()) {
                moreChunks = false;
            }
        }    
        decodeImage();
    }

    private void decodeIHDR(BigEndianDecoder coder) throws DataFormatException
    {
        width = coder.readWord(4, false);
        height = coder.readWord(4, false);
        attributes[BIT_DEPTH] = coder.readByte();
        attributes[COLOUR_TYPE] = coder.readByte();
        attributes[COMPRESSION] = coder.readByte();
        attributes[FILTER_METHOD] = coder.readByte();
        attributes[INTERLACE_METHOD] = coder.readByte();
        
        coder.readWord(4, false); // crc

        switch (attributes[COLOUR_TYPE])
        {
            case GREYSCALE: 
            	format = (attributes[TRANSPARENT_GREY] == -1) ? ImageFormat.RGB8 : ImageFormat.RGBA; 
            	attributes[COLOUR_COMPONENTS] = 1; 
            	break;
            case TRUE_COLOUR: 
            	format = (attributes[TRANSPARENT_RED] == -1) ? ImageFormat.RGB8 : 
            		ImageFormat.RGBA; attributes[COLOUR_COMPONENTS] = 3; 
            	break;
            case INDEXED_COLOUR: 
            	format = ImageFormat.IDX8; 
            	attributes[COLOUR_COMPONENTS] = 1; 
            	break;
            case ALPHA_GREYSCALE: 
            	format = ImageFormat.RGBA; 
            	attributes[COLOUR_COMPONENTS] = 2; 
            	break;
            case ALPHA_TRUECOLOUR: 
            	format = ImageFormat.RGBA; 
            	attributes[COLOUR_COMPONENTS] = 4; 
            	break;
            default: 
            	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }  
    }

    private void decodePLTE(BigEndianDecoder coder, int length)
    {
        if (attributes[COLOUR_TYPE] == 3)
        {
            int paletteSize = length / 3;
            int index = 0;
            
            table = new byte[paletteSize*4];
            
            for (int i=0; i<paletteSize; i++, index+=4)
            {
                table[index+3] = (byte)0xFF;
                table[index+2] = (byte)coder.readByte();
                table[index+1] = (byte)coder.readByte();
                table[index] = (byte)coder.readByte();
            }
        }
        else
        {
            coder.adjustPointer(length << 3);
        }        
        coder.readWord(4, false); // crc
    }

    private void decodeTRNS(BigEndianDecoder coder, int length)
    {
    	int index = 0;
    	
        switch(attributes[COLOUR_TYPE])
        {
            case GREYSCALE:
                attributes[TRANSPARENT_GREY] = coder.readWord(2, false);
                break;
            case TRUE_COLOUR:
                attributes[TRANSPARENT_RED] = coder.readWord(2, false);
                attributes[TRANSPARENT_GREEN] = coder.readWord(2, false);
                attributes[TRANSPARENT_BLUE] = coder.readWord(2, false);
                break;
            case INDEXED_COLOUR:
                format = ImageFormat.IDXA;
                for (int i=0; i<length; i++, index+=4) 
                {
                    table[index+3] = (byte)coder.readByte();

                    if (table[index+3] == 0) {
                        table[index] = 0;
                        table[index+1] = 0;
                        table[index+2] = 0;
                    }
                }
                break;
            default:
                break;
        }
        coder.readWord(4, false); // crc
    }

    private void decodeIDAT(BigEndianDecoder coder, int length)
    {
        int currentLength = chunkData.length;
        int newLength = currentLength + length;
        
        byte[] data = new byte[newLength];
        
        System.arraycopy(chunkData, 0, data, 0, currentLength);

        for (int i=currentLength; i<newLength; i++) {
            data[i] = (byte)coder.readByte();
        }
            
        chunkData = data;
        
        coder.readWord(4, false); // crc
    }

    private void decodeImage() throws DataFormatException
    {
        if (format == ImageFormat.RGB8 && attributes[BIT_DEPTH] <= 5) {
            format = ImageFormat.RGB5;
        }

        if (format == ImageFormat.RGB5 || format == ImageFormat.RGB8 || format == ImageFormat.RGBA) {
            image = new byte[height*width*4];
        }
            
        if (format == ImageFormat.IDX8 || format == ImageFormat.IDXA) {
            image = new byte[height*width];
        }
            
        byte[] encodedImage = unzip(chunkData);
        
        int bitsPerPixel = attributes[BIT_DEPTH]*attributes[COLOUR_COMPONENTS];
        int bitsPerRow = width * bitsPerPixel;
        int rowWidth = (bitsPerRow % 8 > 0) ? (bitsPerRow/8)+1 : (bitsPerRow/8);
        int bytesPerPixel = (bitsPerPixel<8) ? 1 : bitsPerPixel/8;
        
        byte[] current = new byte[rowWidth];
        byte[] previous = new byte[rowWidth];
        
        for (int i=0; i<rowWidth; i++) {
            previous[i] = (byte)0;
        }
            
        int rowStart = 0;
        int rowInc = 0;
        int colStart = 0;
        int colInc = 0;
        
        int imageIndex = 0;
        int pixelCount = 0;
        
        int row = 0;
        int col = 0;
        int filter = 0;
        
        int scanBits = 0;
        int scanLength = 0;
        
        int numberOfPasses = (attributes[INTERLACE_METHOD] == 1) ? 7 : 1;

        int xc = 0;
        int xp = 0;

        for (int pass=0; pass<numberOfPasses; pass++)
        {
            rowStart = (attributes[INTERLACE_METHOD] == 1) ? startRow[pass] : 0;
            rowInc = (attributes[INTERLACE_METHOD] == 1) ? rowIncrement[pass] : 1;
            
            colStart = (attributes[INTERLACE_METHOD] == 1) ? startColumn[pass] : 0;
            colInc = (attributes[INTERLACE_METHOD] == 1) ? columnIncrement[pass] : 1;
            
            for (row=rowStart; row<height && imageIndex<encodedImage.length; row+=rowInc)
            {
                for (col=colStart, pixelCount=0, scanBits=0; col<width; col += colInc) {
                	pixelCount++;
                	scanBits+=bitsPerPixel;
                }

                scanLength = (scanBits%8 > 0) ? (scanBits/8)+1 : (scanBits/8);

                filter = encodedImage[imageIndex++];
                              
                for (int i=0; i<scanLength; i++, imageIndex++) {
                    current[i] = (imageIndex < encodedImage.length) ? encodedImage[imageIndex] : previous[i];
                }

                switch (filter) 
                {
                    case NO_FILTER:
                        break;
                    case SUB_FILTER:
                        for (xc = bytesPerPixel, xp = 0; xc < scanLength; xc++, xp++) {
                            current[xc] = (byte)(current[xc] + current[xp]);
                        }
                        break;
                    case UP_FILTER:
                        for (xc = 0; xc < scanLength; xc++) {
                            current[xc] = (byte)(current[xc] + previous[xc]);
                        }
                        break;
                    case AVG_FILTER:
                        for (xc = 0; xc < bytesPerPixel; xc++) {
                            current[xc] = (byte)(current[xc] + (0 + (0xFF & previous[xc])) / 2);
                        }
                        
                        for (xc = bytesPerPixel, xp = 0; xc < scanLength; xc++, xp++) {
                            current[xc] = (byte)(current[xc] + ((0xFF & current[xp]) + (0xFF & previous[xc])) / 2);
                        }
                        break;
                    case PAETH_FILTER:
                        for (xc = 0; xc < bytesPerPixel; xc++) {
                            current[xc] = (byte)(current[xc] + paeth((byte)0, previous[xc], (byte)0));
                        }
                        
                        for (xc = bytesPerPixel, xp = 0; xc < scanLength; xc++, xp++) {
                            current[xc] = (byte)(current[xc] + paeth(current[xp], previous[xc], previous[xp]));
                        }
                        break;
                    default:
                    	throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
                }
        
                System.arraycopy(current, 0, previous, 0, scanLength);
                        
                BigEndianDecoder coder = new BigEndianDecoder(current);
                
                for (col=colStart; col<width; col+=colInc)
                {
                    switch (attributes[COLOUR_TYPE])
                    {
                        case GREYSCALE: decodeGreyscale(coder, row, col); break;
                        case TRUE_COLOUR: decodeTrueColour(coder, row, col); break;
                        case INDEXED_COLOUR: decodeIndexedColour(coder, row, col); break;
                        case ALPHA_GREYSCALE: decodeAlphaGreyscale(coder, row, col); break;
                        case ALPHA_TRUECOLOUR: decodeAlphaTrueColour(coder, row, col); break;
                        default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT); 
                    }        
                }
            }
        }
    }

    private int paeth(byte L, byte u, byte nw)
    {
        int a = 0xFF & L;
        int b = 0xFF & u; 
        int c = 0xFF & nw; 
        int p = a + b - c;
        int pa = p - a; 
        
        if (pa < 0) { 
        	pa = -pa;
        }
        
        int pb = p - b; 
        
        if (pb < 0) {
        	pb = -pb; 
        }
        
        int pc = p - c;
        
        if (pc < 0) {
        	pc = -pc; 
        }
        
        if (pa <= pb && pa <= pc) {
        	return a;
        }
        
        if (pb <= pc) {
        	return b;
        }
        
        return c;
    }

    private void decodeGreyscale(BigEndianDecoder coder, int row, int col) throws DataFormatException
    {
        int pixel = 0;
        byte colour = 0;
        
        switch (attributes[BIT_DEPTH])
        {
            case 1:  pixel = coder.readBits(1, false); colour = (byte) MONOCHROME[pixel]; break;
            case 2:  pixel = coder.readBits(2, false); colour = (byte) GREYCSALE2[pixel]; break;
            case 4:  pixel = coder.readBits(4, false); colour = (byte) GREYCSALE4[pixel]; break;
            case 8:  pixel = coder.readByte(); colour = (byte) pixel; break;
            case 16: pixel = coder.readWord(2, false); colour = (byte) (pixel >> 8); break;
            default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }                    

        image[row*width+col] = colour;
        image[row*width+col+1] = colour;
        image[row*width+col+2] = colour;
        image[row*width+col+3] = (byte)attributes[TRANSPARENT_GREY];
    }
    
    private void decodeTrueColour(BigEndianDecoder coder, int row, int col) throws DataFormatException
    {
        int pixel = 0;
        byte colour = 0;
        
        for (int i=0; i<attributes[COLOUR_COMPONENTS]; i++)
        {
        	if (attributes[BIT_DEPTH] == 8) {
        		pixel = coder.readByte(); 
        		colour = (byte) pixel;
        	}
        	else if (attributes[BIT_DEPTH] == 16) {
        		pixel = coder.readWord(2, false); 
        		colour = (byte) (pixel >> 8);
        	}
        	else {
        		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        	}

        	image[row*width+col+i] = colour;
        }
        image[row*width+col+3] = (byte)attributes[TRANSPARENT_RED];
    }
    
    private void decodeIndexedColour(BigEndianDecoder coder, int row, int col) throws DataFormatException
    {
        int index = 0;
        
        switch (attributes[BIT_DEPTH])
        {
            case 1:  index = coder.readBits(1, false); break;
            case 2:  index = coder.readBits(2, false); break;
            case 4:  index = coder.readBits(4, false); break;
            case 8:  index = coder.readByte(); break;
            case 16: index = coder.readWord(2, false); break;
            default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }                    
        image[row*width+col] = (byte)index;
    }
    
    private void decodeAlphaGreyscale(BigEndianDecoder coder, int row, int col) throws DataFormatException
    {
        int pixel = 0;
        byte colour = 0;
        int alpha = 0;
        
        switch (attributes[BIT_DEPTH])
        {
            case 1:  pixel = coder.readBits(1, false); colour = (byte) MONOCHROME[pixel]; alpha = coder.readBits(1, false); break;
            case 2:  pixel = coder.readBits(2, false); colour = (byte) GREYCSALE2[pixel]; alpha = coder.readBits(2, false); break;
            case 4:  pixel = coder.readBits(4, false); colour = (byte) GREYCSALE4[pixel]; alpha = coder.readBits(4, false); break;
            case 8:  pixel = coder.readByte(); colour = (byte) pixel; alpha = coder.readByte(); break;
            case 16: pixel = coder.readWord(2, false); colour = (byte) (pixel >> 8); alpha = coder.readWord(2, false) >> 8; break;
            default: throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }                    

        image[row*width+col] = colour;
        image[row*width+col+1] = colour;
        image[row*width+col+2] = colour;
        image[row*width+col+3] = (byte) alpha;
    }
    
    private void decodeAlphaTrueColour(BigEndianDecoder coder, int row, int col) throws DataFormatException
    {
        int pixel = 0;
        byte colour = 0;
        
        for (int i=0; i<attributes[COLOUR_COMPONENTS]; i++)
        {
        	if (attributes[BIT_DEPTH] == 8) {
        		pixel = coder.readByte(); 
        		colour = (byte) pixel; 
        	}
        	else if (attributes[BIT_DEPTH] == 16) {
        		pixel = coder.readWord(2, false); 
        		colour = (byte) (pixel >> 8);
        	}
        	else {
        		throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        	}

            image[row*width+col+i] = colour;
        }
    }

    private byte[] unzip(byte[] bytes) throws DataFormatException
    {
        byte[] data = new byte[width*height*8];
        int count = 0;
        
        Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        count = inflater.inflate(data);
        
        byte[] uncompressedData = new byte[count];
        
        System.arraycopy(data, 0, uncompressedData, 0, count);

        return uncompressedData;
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

		FileInputStream stream = null; //TODO(code) fix

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

    public int getWidth() {
    	return width;
    }
    public int getHeight() {
    	return height;
    }
    
    public byte[] getImage() {
    	return Arrays.copyOf(image, image.length);
    }
}
