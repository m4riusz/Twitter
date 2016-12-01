package com.twitter.util.extractor;

import com.twitter.model.User;

import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */
public interface UsernameExtractor {
    List<String> extract(String text);
}
