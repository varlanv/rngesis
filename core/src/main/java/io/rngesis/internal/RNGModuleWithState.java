package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;

import java.util.Random;

public class RNGModuleWithState<T> implements RNGesisModule<T> {

    TriFunction<RNGesis, Random, NewModuleState, T> function;

    @Override
    public T next(RNGesis rnGesis, Random random, NewModuleState operation) {
        return function.apply(rnGesis, random, operation);
    }
}
