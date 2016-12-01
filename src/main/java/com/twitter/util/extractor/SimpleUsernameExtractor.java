package com.twitter.util.extractor;

import com.twitter.model.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */
public class SimpleUsernameExtractor implements UsernameExtractor {

    public static final String SPACE = " ";
    public static final String AT_SIGN = "@";

    @Override
    public List<String> extract(String text) {
        String[] words = text.split(SPACE);
        List<String> usernames = new ArrayList<>();
        List<String> usernameList = new ArrayList<>();

        for (String word : words) {
            if (beginsWithAtSign(word) &&
                    hasEnoughLength(word) &&
                    containsOnlyOneHash(word) &&
                    firstOccurrence(usernames, word) &&
                    containsOnlyDigsAndLetters(word.substring(1))
                    ) {
                String wordWithoutAtSign = word.substring(1);
                usernames.add(wordWithoutAtSign);
                usernameList.add(wordWithoutAtSign);
            }
        }
        return usernameList;
    }

    private boolean containsOnlyDigsAndLetters(String substring) {
        for (Character c : substring.toCharArray()) {
            if (!(Character.isLetter(c) || Character.isDigit(c)) || "_".equals(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean firstOccurrence(List<String> tags, String word) {
        return !tags.contains(word.substring(1));
    }

    private boolean containsOnlyOneHash(String word) {
        return !word.substring(1).contains(AT_SIGN);
    }

    private boolean hasEnoughLength(String word) {
        return word.length() > 1;
    }

    private boolean beginsWithAtSign(String word) {
        return word.startsWith(AT_SIGN);
    }


}
