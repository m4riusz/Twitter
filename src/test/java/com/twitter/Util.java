package com.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariusz on 19.07.16.
 */
public class Util {

    public static <T> List<T> aListWith(T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }

    public static <T> T a(Builder<T> builder) {
        return builder.build();
    }
}
