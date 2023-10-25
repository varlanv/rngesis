package io.rngesis.api;

import io.rngesis.internal.NewModuleState;

import java.util.Random;

public interface RNGesisModule<T> {

    T next(RNGesis rnGesis, Random random, NewModuleState operation);

    default boolean isStateless() {
        return false;
    };
}
