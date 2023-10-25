package io.rngesis.internal;

import io.rngesis.api.RNGesisModule;
import lombok.Value;

import java.lang.reflect.Method;

@Value
public class MethodWithParameter {

    Method method;
    RNGesisModule<?> module;
    Class<?> parameterType;
    Class<?> genericParameterType;
}
