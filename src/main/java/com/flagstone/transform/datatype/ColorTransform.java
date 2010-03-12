/*
 * ColorTransform.java
 * Transform
 *
 * Copyright (c) 2001-2010 Flagstone Software Ltd. All rights reserved.
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
package com.flagstone.transform.datatype;

import com.flagstone.transform.Constants;
import com.flagstone.transform.coder.Coder;
import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.Context;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncodeable;
import com.flagstone.transform.coder.SWFEncoder;

/**
 * A ColorTransform is used to change the colour of a shape or button without
 * changing the values in the original definition of the object.
 *
 * <p>
 * Two types of transformation are supported: Add and Multiply. In Add
 * transformations a value is added to each colour channel:
 * </p>
 *
 * <pre>
 * newRed = red + addRedTerm
 * newGreen = green + addGreenTerm
 * newBlue = blue + addBlueTerm
 * newAlpha = alpha + addAlphaTerm
 * </pre>
 *
 * <p>
 * In Multiply transformations each colour channel is multiplied by a given
 * value:
 * </p>
 *
 * <pre>
 * newRed = red * multiplyRedTerm
 * newGreen = green * multiplyGreenTerm
 * newBlue = blue * multiplyBlueTerm
 * newAlpha = alpha * multiplyAlphaTerm
 * </pre>
 *
 * <p>
 * Add and Multiply transforms may be combined in which case the multiply terms
 * are applied to the colour channel before the add terms.
 * </p>
 *
 * <pre>
 * newRed = (red * multiplyRedTerm) + addRedTerm
 * newGreen = (green * multiplyGreenTerm) + addGreenTerm
 * newBlue = (blue * multiplyBlueTerm) + addBlueTerm
 * newAlpha = (alpha * multiplyAlphaTerm) + addAlphaTerm
 * </pre>
 *
 * <p>
 * For each type of transform the result of the calculation is limited to the
 * range 0..255. If the result is less than 0 or greater than 255 then it is
 * clamped at 0 and 255 respectively.
 * </p>
 *
 * <p>
 * Not all objects containing a colour transform use the add or multiply terms
 * defined for the alpha channel. The colour objects defined in an DefineButton,
 * ButtonColorTransform or PlaceObject object do not use the alpha channel while
 * DefineButton2 and PlaceObject2 do. Whether the parent object uses the alpha
 * channel is stored in a SWFContext which is passed when the transform is
 * encoded or decoded.
 * </p>
 *
 */
public final class ColorTransform implements SWFEncodeable {

    private static final String FORMAT = "ColorTransform: { "
            + "multiply=[%f, %f, %f, %f]; add=[%d, %d, %d, %d] }";

    private static final int FIELD_SIZE = 4;
    private static final float SCALE_MULTIPLY = 256.0f;
    private static final int DEFAULT_MULTIPLY = 256;
    private static final int DEFAULT_ADD = 0;

    private final transient int multiplyRed;
    private final transient int multiplyGreen;
    private final transient int multiplyBlue;
    private final transient int multiplyAlpha;

    private final transient int addRed;
    private final transient int addGreen;
    private final transient int addBlue;
    private final transient int addAlpha;

    private final transient boolean hasMultiply;
    private final transient boolean hasAdd;

    private transient int size;
    private transient boolean hasAlpha;

    /**
     * Creates and initialises a ColorTransform object using values encoded
     * in the Flash binary format.
     *
     * @param coder
     *            an SWFDecoder object that contains the encoded Flash data.
     *
     * @param context
     *            a Context object used to manage the decoders for different
     *            type of object and to pass information on how objects are
     *            decoded.
     *
     * @throws CoderException
     *             if an error occurs while decoding the data.
     */
    public ColorTransform(final SWFDecoder coder, final Context context)
            throws CoderException {

        hasAdd = coder.readBits(1, false) != 0;
        hasMultiply = coder.readBits(1, false) != 0;
        hasAlpha = context.getVariables().containsKey(Context.TRANSPARENT);
        size = coder.readBits(FIELD_SIZE, false);

        if (hasMultiply) {
            multiplyRed = coder.readBits(size, true);
            multiplyGreen = coder.readBits(size, true);
            multiplyBlue = coder.readBits(size, true);
            
            if (hasAlpha) {
                multiplyAlpha = coder.readBits(size, true);
            } else {
                multiplyAlpha = DEFAULT_MULTIPLY;
            }
        } else {
            multiplyRed = DEFAULT_MULTIPLY;
            multiplyGreen = DEFAULT_MULTIPLY;
            multiplyBlue = DEFAULT_MULTIPLY;
            multiplyAlpha = DEFAULT_MULTIPLY;
        }

        if (hasAdd) {
            addRed = coder.readBits(size, true);
            addGreen = coder.readBits(size, true);
            addBlue = coder.readBits(size, true);
            
            if (hasAlpha) {
                addAlpha = coder.readBits(size, true);
            } else {
                addAlpha = DEFAULT_ADD;
            }
        } else {
            addRed = DEFAULT_ADD;
            addGreen = DEFAULT_ADD;
            addBlue = DEFAULT_ADD;
            addAlpha = DEFAULT_ADD;
        }

        coder.alignToByte();
    }

    /**
     * Creates an add colour transform.
     *
     * @param addRed
     *            value to add to the red colour channel.
     * @param addGreen
     *            value to add to the green colour channel.
     * @param addBlue
     *            value to add to the blue colour channel.
     * @param addAlpha
     *            value to add to the alpha colour channel.
     */
    public ColorTransform(final int addRed, final int addGreen,
            final int addBlue, final int addAlpha) {
        multiplyRed = DEFAULT_MULTIPLY;
        multiplyGreen = DEFAULT_MULTIPLY;
        multiplyBlue = DEFAULT_MULTIPLY;
        multiplyAlpha = DEFAULT_MULTIPLY;

        this.addRed = addRed;
        this.addGreen = addGreen;
        this.addBlue = addBlue;
        this.addAlpha = addAlpha;

        hasMultiply = false;
        hasAdd = true;
    }

    /**
     * Creates a multiply colour transform.
     *
     * @param mulRed
     *            value to multiply the red colour channel by.
     * @param mulGreen
     *            value to multiply the green colour channel by.
     * @param mulBlue
     *            value to multiply the blue colour channel by.
     * @param mulAlpha
     *            value to multiply the alpha colour channel by.
     */
    public ColorTransform(final float mulRed, final float mulGreen,
            final float mulBlue, final float mulAlpha) {
        multiplyRed = (int) (mulRed * SCALE_MULTIPLY);
        multiplyGreen = (int) (mulGreen * SCALE_MULTIPLY);
        multiplyBlue = (int) (mulBlue * SCALE_MULTIPLY);
        multiplyAlpha = (int) (mulAlpha * SCALE_MULTIPLY);

        addRed = DEFAULT_ADD;
        addGreen = DEFAULT_ADD;
        addBlue = DEFAULT_ADD;
        addAlpha = DEFAULT_ADD;

        hasMultiply = true;
        hasAdd = false;
    }

    /**
     * Creates a colour transform that contains add and multiply terms.
     *
     * @param mulRed
     *            value to multiply the red colour channel by.
     * @param mulGreen
     *            value to multiply the green colour channel by.
     * @param mulBlue
     *            value to multiply the blue colour channel by.
     * @param mulAlpha
     *            value to multiply the alpha colour channel by.
     * @param addRed
     *            value to add to the red colour channel.
     * @param addGreen
     *            value to add to the green colour channel.
     * @param addBlue
     *            value to add to the blue colour channel.
     * @param addAlpha
     *            value to add to the alpha colour channel.
     */
    public ColorTransform(final int addRed, final int addGreen,
            final int addBlue, final int addAlpha, final float mulRed,
            final float mulGreen, final float mulBlue, final float mulAlpha) {

        this.addRed = addRed;
        this.addGreen = addGreen;
        this.addBlue = addBlue;
        this.addAlpha = addAlpha;

        multiplyRed = (int) (mulRed * SCALE_MULTIPLY);
        multiplyGreen = (int) (mulGreen * SCALE_MULTIPLY);
        multiplyBlue = (int) (mulBlue * SCALE_MULTIPLY);
        multiplyAlpha = (int) (mulAlpha * SCALE_MULTIPLY);

        hasMultiply = true;
        hasAdd = true;
   }

    /**
     * Returns the value of the multiply term for the red channel.
     */
    public float getMultiplyRed() {
        return multiplyRed / SCALE_MULTIPLY;
    }

    /**
     * Returns the value of the multiply term for the green channel.
     */
    public float getMultiplyGreen() {
        return multiplyGreen / SCALE_MULTIPLY;
    }

    /**
     * Returns the value of the multiply term for the blue channel.
     */
    public float getMultiplyBlue() {
        return multiplyBlue / SCALE_MULTIPLY;
    }

    /**
     * Returns the value of the multiply term for the alpha channel.
     */
    public float getMultiplyAlpha() {
        return multiplyAlpha / SCALE_MULTIPLY;
    }

    /**
     * Returns the value of the add term for the red channel.
     */
    public int getAddRed() {
        return addRed;
    }

    /**
     * Returns the value of the add term for the green channel.
     */
    public int getAddGreen() {
        return addGreen;
    }

    /**
     * Returns the value of the add term for the blue channel.
     */
    public int getAddBlue() {
        return addBlue;
    }

    /**
     * Returns the value of the add term for the alpha channel.
     */
    public int getAddAlpha() {
        return addAlpha;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(FORMAT, getMultiplyRed(), getMultiplyGreen(),
                getMultiplyBlue(), getMultiplyAlpha(),
                addRed, addGreen, addBlue, addAlpha);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object object) {
        boolean result;
        ColorTransform transform;

        if (object == null) {
            result = false;
        } else if (object == this) {
            result = true;
        } else if (object instanceof ColorTransform) {
            transform = (ColorTransform) object;
            result = (addRed == transform.addRed)
                    && (addGreen == transform.addGreen)
                    && (addBlue == transform.addBlue)
                    && (addAlpha == transform.addAlpha)
                    && (multiplyRed == transform.multiplyRed)
                    && (multiplyGreen == transform.multiplyGreen)
                    && (multiplyBlue == transform.multiplyBlue)
                    && (multiplyAlpha == transform.multiplyAlpha);
        } else {
            result = false;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return ((((((addRed * Constants.PRIME + addGreen)
                * Constants.PRIME + addBlue)
                * Constants.PRIME + addAlpha)
                * Constants.PRIME + multiplyRed)
                * Constants.PRIME + multiplyGreen)
                * Constants.PRIME + multiplyBlue)
                * Constants.PRIME + multiplyAlpha;
    }

    /** {@inheritDoc} */
    public int prepareToEncode(final SWFEncoder coder, final Context context) {

        int numberOfBits = 2 + FIELD_SIZE + Coder.BYTE_ALIGN;

        hasAlpha = context.getVariables().containsKey(Context.TRANSPARENT);
        size = 0;

        if (hasMultiply) {
            sizeTerms(multiplyRed, multiplyGreen, multiplyBlue, multiplyAlpha);
        }

        if (hasAdd) {
            sizeTerms(addRed, addGreen, addBlue, addAlpha);
        }

        if (hasMultiply) {
            numberOfBits += size * (hasAlpha ? Color.RGBA : Color.RGB);
        }

        if (hasAdd) {
            numberOfBits += size * (hasAlpha ? Color.RGBA : Color.RGB);
        }

        return numberOfBits >> Coder.BITS_TO_BYTES;
    }

    /** {@inheritDoc} */
    public void encode(final SWFEncoder coder, final Context context)
            throws CoderException {

        coder.writeBits(hasAdd ? 1 : 0, 1);
        coder.writeBits(hasMultiply ? 1 : 0, 1);
        coder.writeBits(size, FIELD_SIZE);

        if (hasMultiply) {
            encodeTerms(multiplyRed, multiplyGreen, multiplyBlue, 
                    multiplyAlpha, coder);
        }

        if (hasAdd) {
            encodeTerms(addRed, addGreen, addBlue, addAlpha, coder);
        }

        coder.alignToByte();
    }
    
    private void sizeTerms(final int red, final int green, final int blue, 
            final int alpha) {
        size = Math.max(size, SWFEncoder.size(red));
        size = Math.max(size, SWFEncoder.size(green));
        size = Math.max(size, SWFEncoder.size(blue));

        if (hasAlpha) {
            size = Math.max(size, SWFEncoder.size(alpha));
        }
    }
    
    private void encodeTerms(final int red, final int green, final int blue, 
            final int alpha, final SWFEncoder coder) {
        coder.writeBits(red, size);
        coder.writeBits(green, size);
        coder.writeBits(blue, size);
        
        if (hasAlpha) {
            coder.writeBits(alpha, size);
        }
    }
}
