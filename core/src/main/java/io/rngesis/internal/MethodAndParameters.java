package io.rngesis.internal;

import lombok.Value;

import java.util.List;

@Value
public class MethodAndParameters {

    List<MethodWithParameter> methodWithParameters;

    public boolean isEmpty() {
        return methodWithParameters.isEmpty();
    }
}
