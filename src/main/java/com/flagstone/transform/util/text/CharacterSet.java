package com.flagstone.transform.util.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CharacterSet {

    private final transient Set<Character>characters =
        new LinkedHashSet<Character>();

    public void add(char character) {
        characters.add(character);
    }

    public void add(String text) {
        for (int i = 0; i < text.length(); i++) {
            characters.add(text.charAt(i));
        }
    }

    public List<Character> getCharacters() {
        final List<Character> list = new ArrayList<Character>(characters);
        Collections.sort(list);
        return list;
    }
}
