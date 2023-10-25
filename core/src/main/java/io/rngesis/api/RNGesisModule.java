package io.rngesis.api;

import io.rngesis.internal.NewModuleState;

import java.util.Random;

public interface RNGesisModule<T> {

    T next(RNGesis rnGesis, Random random, NewModuleState state);

    default boolean isStateless() {
        return false;
    };
}
