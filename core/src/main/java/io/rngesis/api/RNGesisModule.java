package io.rngesis.api;

import java.util.Random;

public interface RNGesisModule<T> {

    T next(RNGesis rnGesis, Random random);

    default boolean isStateless() {
        return false;
    };
}
