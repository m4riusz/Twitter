package com.twitter.util;

import com.twitter.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mariusz on 02.08.16.
 */
@Component
public interface TagExtractor {
    List<Tag> extract(String string);
}
