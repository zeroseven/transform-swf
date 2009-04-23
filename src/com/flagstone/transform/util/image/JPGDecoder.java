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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.zip.DataFormatException;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.FLVDecoder;
import com.flagstone.transform.coder.ImageTag;
import com.flagstone.transform.image.DefineJPEGImage2;

/**
 * JPGDecoder decodes JPEG images so they can be used in a Flash file.
 */
public final class JPGDecoder implements ImageProvider, ImageDecoder
{
	private int width;
	private int height;
    private byte[] image;

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
    	return new DefineJPEGImage2(identifier, image); 
    }

    public ImageDecoder newDecoder() {
    	return new JPGDecoder();
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
    
	protected void decode(byte[] data) throws DataFormatException
    {
        image = new byte[data.length];
        
        System.arraycopy(data, 0, image, 0, data.length);

        if (!jpegInfo()) {
            throw new DataFormatException(Strings.UNSUPPORTED_FILE_FORMAT);
        }
    	
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

	private boolean jpegInfo()
	{
		FLVDecoder coder = new FLVDecoder(image);
		
		boolean result;

		if (coder.readWord(2, false) == 0xffd8) 
		{
			int marker;
			
			do {
				marker = coder.readWord(2, false);
				
				if ((marker & 0xff00) == 0xff00)
				{
					if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4
									&& marker != 0xffc8)
					{
						coder.adjustPointer(24);
						coder.readWord(2, false);
						coder.readWord(2, false);
						break;
					} 
					else
					{
						coder.adjustPointer((coder.readWord(2, false) - 2) << 3);
					}
				}
				
			} while ((marker & 0xff00) == 0xff00);
			
			result = true;
		}
		else {
			result = false;
		}
		return result;
	}
}
