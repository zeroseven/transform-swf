/*
 * CharacterSet.java
 * Transform
 *
 * Copyright (c) 2010 Flagstone Software Ltd. All rights reserved.
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

package com.flagstone.transform.util.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * CharacterSet is a convenience class for created a sorted list of characters
 * that can be used to create a font definition.
 */
public final class CharacterSet {

    /** The set of characters. */
    private final transient Set<Character>characters =
        new LinkedHashSet<Character>();

    /**
     * Add a character to the set of existing characters.
     * @param character a character. If a character is already included then it
     * is ignored.
     */
    public void add(final char character) {
        characters.add(character);
    }

    /**
     * Add all the characters in a string to the set of existing characters.
     * @param text a string of characters. If any character is already included
     * then it is ignored.
     */
    public void add(final String text) {
        for (int i = 0; i < text.length(); i++) {
            characters.add(text.charAt(i));
        }
    }

    /**
     * Get the sorted list of characters.
     *
     * @return a list containing the characters in ascending order.
     */
    public List<Character> getCharacters() {
        final List<Character> list = new ArrayList<Character>(characters);
        Collections.sort(list);
        return list;
    }
}
