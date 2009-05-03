/*
 * FontInfo.java
 * Transform
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

package com.flagstone.transform.text;

import com.flagstone.transform.Strings;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.MovieTag;
import com.flagstone.transform.coder.MovieTypes;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * TextSettings allows you to control how individual text fields are rendered.
 *
 * <p>
 * There are four parameters that control how the text is rendered:
 * </p>
 * <ol>
 * <li>Advanced Rendering - whether the text is rendered using the advanced
 * anti-aliasing engine added in Flash 8.</li>
 * <li>Grid Alignment - how letters are aligned with respect to the pixel grid
 * used in LCD monitors.</li>
 * <li>Thickness - a parameter used to control the thickness of the line when
 * anti-aliasing is used.</li>
 * <li>Sharpness - a parameter used to control the sharpness of the line when
 * anti-aliasing is used.</li>
 * </ol>
 * <p>
 * The thickness and sharpness control the how the text is rendered:
 *
 * <pre>
 *    outsideCutoff = (0.5 * sharpness - thickness) * fontSize
 *    insideCutoff = (-0.5 * sharpness - thickness) * fontSize
 * </pre>
 *
 * Note that Adobe reports the results can be poor when the text is scaled by a
 * significant amount and so the default values of 0.0 should be used for the
 * thickness and sharpness values.
 * </p>
 */
//TODO(class)
public final class TextSettings implements MovieTag {
    /**
     * Grid specifies how letters are aligned with respect to the pixel grid on
     * a screen.
     */
    public enum Grid {
        /** Do not use grid fitting. */
        NONE,
        /** Align letters on pixel boundaries. */
        PIXEL,
        /** Align letters on 1/3 pixel boundaries. */
        SUBPIXEL
    }

    private static final String FORMAT = "TextSettings: { identifier=%d;"
            + " useAdvanced=%s, grid=%s, thickness=%f, sharpness=%f }";

    private transient int identifier;
    private transient int rendering;
    private transient int thickness;
    private transient int sharpness;

    /**
     * Creates and initialises an TextSettings using values encoded in the Flash
     * binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public TextSettings(final SWFDecoder coder) throws CoderException {
        if ((coder.readWord(2, false) & 0x3F) == 0x3F) {
            coder.readWord(4, false);
        }

        identifier = coder.readWord(2, false);
        rendering = coder.readByte();
        thickness = coder.readWord(4, false);
        sharpness = coder.readWord(4, false);
        coder.readByte();
    }

    /**
     * Creates a TextSettings object with the specified values.
     *
     * @param uid
     *            the unique identifier of an existing text field.
     * @param advanced
     *            whether the advanced rendering engine will be used to display
     *            the text.
     * @param grid
     *            how letters are aligned with respect to the pixel grid.
     * @param thickness
     *            the thickness used when anti-aliasing the text.
     * @param sharpness
     *            the sharpness used when anti-aliasing the text.
     */
    public TextSettings(final int uid, final boolean advanced, final Grid grid,
            final float thickness, final float sharpness) {
        setIdentifier(uid);
        useAdvanced(advanced);
        setGrid(grid);
        setThickness(thickness);
        setSharpness(sharpness);
    }

    /**
     * Creates an TextSettings object and initialised it by copying the values
     * from an existing one.
     *
     * @param object
     *            a TextSettings object.
     */
    public TextSettings(final TextSettings object) {
        identifier = object.identifier;
        rendering = object.rendering;
        thickness = object.thickness;
        sharpness = object.sharpness;
    }

    /**
     * Returns the unique identifier of the text definition that this object
     * applies to.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of the text definition that this object applies to.
     *
     * @param uid
     *            the unique identifier of an DefineText, DefineText2 or
     *            DefineTextField object. Must be in the range 1..65535.
     */
    public void setIdentifier(final int uid) {
        if ((uid < 1) || (uid > 65535)) {
            throw new IllegalArgumentException(Strings.IDENTIFIER_RANGE);
        }
        identifier = uid;
    }

    /**
     * Returns true if the advanced text rendering engine, introduced in Flash 8
     * will be used.
     */
    public boolean useAdvanced() {
        return (rendering & 0x40) != 0;
    }

    /**
     * Sets whether the advanced text rendering engine (true) or standard engine
     * (false) will be used to render the text.
     */
    public void useAdvanced(final boolean flag) {
        rendering |= 0x40;
    }

    /**
     * Returns the alignment of letters with respect to the pixel grid.
     *
     * @return the alignment, either NONE, PIXEL or SUBPIXEL.
     */
    public Grid getGrid() {
        Grid alignment;

        switch (rendering & 0x38) {
        case 8:
            alignment = Grid.PIXEL;
            break;
        case 16:
            alignment = Grid.SUBPIXEL;
            break;
        default:
            alignment = Grid.NONE;
            break;
        }
        return alignment;
    }

    /**
     * Selects how the text letters will be aligned with respect to the pixel
     * grid used in LCD screens.
     *
     * @param alignment
     *            the alignment with respect to the pixel grid, either NONE,
     *            PIXEL or SUBPIXEL.
     */
    public void setGrid(final Grid alignment) {

        rendering &= 0xC0;

        switch (alignment) {
        case PIXEL:
            rendering |= 0x40;
            break;
        case SUBPIXEL:
            rendering |= 0x40;
            break;
        default:
            break;
        }
    }

    /**
     * Returns the value used to control the thickness of a line when rendered.
     * May be set to 0.0 if the default anti-aliasing value will be used.
     */
    public float getThickness() {
        return thickness / 65536.0f;
    }

    /**
     * Sets the value used to control the thickness of a line when rendered. May
     * be set to 0.0 if the default anti-aliasing value will be used.
     *
     * @param thickness
     *            the value of the thickness parameter used by the rendering
     *            engine.
     */
    public void setThickness(final float thickness) {
        this.thickness = (int) (thickness * 65536);
    }

    /**
     * Returns the value used to control the sharpness of a line when rendered.
     * May be set to 0.0 if the default anti-aliasing value will be used.
     */
    public float getSharpness() {
        return sharpness / 65536.0f;
    }

    /**
     * Sets the value used to control the sharpness of a line when rendered. May
     * be set to 0.0 if the default anti-aliasing value will be used.
     *
     * @param sharpness
     *            the value of the sharpness parameter used by the rendering
     *            engine.
     */
    public void setSharpness(final float sharpness) {
        this.sharpness = (int) (sharpness * 65536);
    }

    /** TODO(method). */
    public TextSettings copy() {
        return new TextSettings(this);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, identifier, String.valueOf(useAdvanced()),
                getGrid(), thickness / 65536.0f, sharpness / 65536.0f);
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {
        return 14;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {
        coder.writeWord((MovieTypes.TEXT_SETTINGS << 6) | 12, 2);
        coder.writeWord(identifier, 2);
        coder.writeByte(rendering);
        coder.writeWord(thickness, 4);
        coder.writeWord(sharpness, 4);
        coder.writeByte(0);
    }
}
