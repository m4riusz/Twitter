package com.twitter.util.extractor;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mariusz on 01.12.16.
 */

@Component
public interface UsernameExtractor {
    List<String> extract(String text);
}
