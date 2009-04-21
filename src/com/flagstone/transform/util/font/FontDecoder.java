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

package com.flagstone.transform.util.font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.zip.DataFormatException;

public interface FontDecoder
{
    /**
     * Initialise this object with the font information decoded from a TrueType 
     * or OpenType font stored in a file.
     * 
     * @param path the path to the file containing the font.
     * 
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws DataFormatException if the there is an error decoding the font.
     */
    public void read(String path) throws FileNotFoundException, IOException, DataFormatException;
    /**
     * Initialise this object with the font information decoded from a TrueType 
     * or OpenType font stored in a file.
     * 
     * @param file the File containing the abstract path to the file containing the font.
     * 
     * @throws FileNotFoundException is the file cannot be found or opened.
     * @throws IOException if there is an error reading the file.
     * @throws DataFormatException if the there is an error decoding the font.
     */
    public void read(File file) throws FileNotFoundException, IOException, DataFormatException;
    public void read(URL url) throws FileNotFoundException, IOException, DataFormatException;
    
    public Font[] getFonts();
}
