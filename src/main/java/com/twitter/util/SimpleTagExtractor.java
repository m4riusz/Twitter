package com.twitter.util;

import com.twitter.model.Tag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariusz on 02.08.16.
 */
@Component
public class SimpleTagExtractor implements TagExtractor {

    public static final String SPACE = " ";
    public static final String HASH = "#";

    @Override
    public List<Tag> extract(String string) {
        String[] words = string.split(SPACE);
        List<String> tags = new ArrayList<>();
        List<Tag> tagList = new ArrayList<>();

        for (String word : words) {
            if (isBeginsWithHash(word) &&
                    hasEnoughtLength(word) &&
                    containsOnlyOneHash(word) &&
                    firstOccurt(tags, word) &&
                    containsOnlyDigsAndLetters(word.substring(1))
                    ) {
                String wordWithoutHash = word.substring(1);
                tags.add(wordWithoutHash);
                tagList.add(new Tag(wordWithoutHash));
            }
        }
        return tagList;
    }

    private boolean containsOnlyDigsAndLetters(String substring) {
        for (char c : substring.toCharArray()) {
            if (!(Character.isLetter(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean firstOccurt(List<String> tags, String word) {
        return !tags.contains(word.substring(1));
    }

    private boolean containsOnlyOneHash(String word) {
        return !word.substring(1).contains(HASH);
    }

    private boolean hasEnoughtLength(String word) {
        return word.length() > 1;
    }

    private boolean isBeginsWithHash(String word) {
        return word.startsWith(HASH);
    }
}
