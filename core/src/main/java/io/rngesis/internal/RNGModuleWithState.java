package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;

import java.util.Random;
import java.util.function.BiFunction;

public class RNGModuleWithState<T> implements RNGesisModule<T> {

    BiFunction<RNGesis, Random, T> function;

    @Override
    public T next(RNGesis rnGesis, Random random) {
        return function.apply(rnGesis, random);
    }
}
