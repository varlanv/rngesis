package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Parameter;
import java.util.Random;

@Setter
@Getter
@RequiredArgsConstructor
public class NewModuleState {

    private final RNGesis rnGesis;
    private final Class<?> rootType;
    private final String name;
    private final Random random;
    private Parameter currentParameter;
    private int iterations = 0;

    void incrementIterations() {
        iterations++;
    }
}
