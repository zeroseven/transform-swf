/*
 * FillStyleDecoder.java
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

package com.flagstone.transform.coder;


import com.flagstone.transform.fillstyle.BitmapFill;
import com.flagstone.transform.fillstyle.FillStyle;
import com.flagstone.transform.fillstyle.FocalGradientFill;
import com.flagstone.transform.fillstyle.GradientFill;
import com.flagstone.transform.fillstyle.SolidFill;

/**
 * Factory is the default implementation of an SWFFactory which used to create
 * instances of Transform classes.
 */
//TODO(class)
public final class FillStyleDecoder implements SWFFactory<FillStyle> {

    /** TODO(method). */
    public SWFFactory<FillStyle> copy() {
        return new FillStyleDecoder();
    }

    /** TODO(method). */
    public FillStyle getObject(final SWFDecoder coder, final Context context)
            throws CoderException {

        FillStyle style;

        switch (coder.scanByte()) {
        case 0:
            style = new SolidFill(coder, context);
            break;
        case 0x10:
            style = new GradientFill(coder, context);
            break;
        case 0x12:
            style = new GradientFill(coder, context);
            break;
        case 0x13:
            style = new FocalGradientFill(coder, context);
            break;
        case 0x40:
            style = new BitmapFill(coder);
            break;
        case 0x41:
            style = new BitmapFill(coder);
            break;
        case 0x42:
            style = new BitmapFill(coder);
            break;
        case 0x43:
            style = new BitmapFill(coder);
            break;
        default:
            throw new CoderException(getClass().getName(), coder.getPointer(),
                    0, 0, "Unsupported FillStyle");
        }
        return style;
    }
}
