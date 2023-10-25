package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class RNGesisModuleWithConstructorParams implements RNGesisModule<Object> {

    private final RNGesisModule<Object> delegate;
    @Getter
    private final Set<String> paramNames;

    public RNGesisModuleWithConstructorParams(RNGesisModule<Object> delegate) {
        this(delegate, Collections.emptySet());
    }

    @Override
    public Object next(RNGesis rnGesis, Random random, NewModuleState state) {
        return delegate.next(rnGesis, random, state);
    }
}
