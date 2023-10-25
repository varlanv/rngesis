package io.rngesis.internal;

import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Value
public class MethodWithParameter {

    Method method;
    Class<?> parameterType;
    Class<?> genericParameterType;
}
