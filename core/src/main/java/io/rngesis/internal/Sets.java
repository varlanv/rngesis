package io.rngesis.internal;

import lombok.val;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {

    public static <T> Set<T> set(T[] elements) {
        val set = new HashSet<T>(Math.max((int) (elements.length / .75f) + 1, 16));
        Collections.addAll(set, elements);
        return set;
    }
}
