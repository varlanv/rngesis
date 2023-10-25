package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import lombok.var;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Value
public class RnNewModule<T> {

    RNGUnknownTypes parent;
    String typeName;
    RNGesis rnGesis;
    Class<T> type;
    Random random;
    NewModuleState state;


    @SneakyThrows
    public RNGesisModule<?> computeNewModule() {
        val module = getRnGesisModule(type);
        if (module != null) {
            return wrapSetters(type, module);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type.getName());
        }
    }

    private RNGesisModule<?> getRnGesisModule(Class<T> type) {
        val constructors = type.getConstructors();
        if (constructors.length == 1) {
            val constructor = constructors[0];
            int modifiers = constructor.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                val parameterTypes = constructor.getGenericParameterTypes();
                if (parameterTypes.length == 0) {
                    return (rnGesis, random, state) -> newInstanceNoParams(constructor);
                } else {
                    final var childModules = prepareChildModules(parameterTypes);
                    return (rnGesis, random, state) -> newInstanceUnknownType(
                            rnGesis,
                            random,
                            childModules,
                            constructor,
                            state
                    );
                }
            }
        } else if (constructors.length > 1) {
            var constructorWithHighestParameterCount = 0;
            Constructor<?> constructorWithHighestParameters = null;
            for (val constructor : constructors) {
                val modifiers = constructor.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    val parameterCount = constructor.getParameterCount();
                    if (parameterCount >= constructorWithHighestParameterCount) {
                        constructorWithHighestParameterCount = parameterCount;
                        constructorWithHighestParameters = constructor;
                    }
                }
            }
            if (constructorWithHighestParameters != null) {
                val fc = constructorWithHighestParameters;
                if (constructorWithHighestParameterCount == 0) {
                    return (rnGesis, random, state) -> newInstanceNoParams(fc);
                } else {
                    val childModules = prepareChildModules(fc.getParameterTypes());
                    return (rnGesis, random, state) -> newInstanceUnknownType(
                            rnGesis,
                            random,
                            childModules,
                            fc,
                            state
                    );
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "No public constructors found for type: " +
                            type.getName() +
                            ". At least one public constructor is required."
            );
        }
        return null;
    }

    private RNGesisModule<?>[] prepareChildModules(Type[] parameterTypes) {
        val childModules = new RNGesisModule<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            val param = parameterTypes[i];
            if (param instanceof ParameterizedType) {
                val parameterizedParam = (ParameterizedType) param;
                val actualTypeArguments = parameterizedParam.getActualTypeArguments();
                val rawType = (Class<?>) parameterizedParam.getRawType();
                if (rawType.isAssignableFrom(List.class)) {
                    val actualTypeArgument = (Class<?>) actualTypeArguments[0];
                    val actualTypeArgumentName = actualTypeArgument.getName();
                    val actualTypeModule = RNGUnknownTypes.modules.getOrCompute(
                            actualTypeArgumentName,
                            () -> RNGUnknownTypes.computeNewModule(
                                    parent,
                                    actualTypeArgumentName,
                                    rnGesis,
                                    actualTypeArgument,
                                    random,
                                    state
                            )
                    );
                    val childModuleName = rawType.getName() + actualTypeArgumentName;
                    childModules[i] = RNGUnknownTypes.modules.getOrCompute(
                            childModuleName,
                            () -> (rnGesis, random, state) -> {
                                val size = random.nextInt(3) + 1;
                                val objects = new ArrayList<>(size);
                                for (var i1 = 0; i1 < size; i1++) {
                                    Object next = actualTypeModule.next(rnGesis, random, state);
                                    objects.add(next);
                                }
                                return objects;
                            });
                }
            } else {
                val rawParam = (Class<?>) param;
                val typeName = rawParam.getName();
                childModules[i] = RNGUnknownTypes.modules.getOrCompute(
                        typeName,
                        () -> RNGUnknownTypes.computeNewModule(
                                parent,
                                typeName,
                                rnGesis,
                                rawParam,
                                random,
                                state
                        )
                );
            }
        }
        return childModules;
    }

    private RNGesisModule<?> wrapSetters(Class<T> type,
                                         RNGesisModule<?> module) {
        val methods = type.getMethods();
        if (methods.length > 0) {
            val setters = buildMethodWithParameters(methods);
            if (!setters.isEmpty()) {
                return (rngesis, random, state) -> {
                    val next = module.next(rngesis, random, state);
                    for (val setter : setters.getMethodWithParameters()) {
                        state.setCurrentParameter(null);
                        invokeSetter(rngesis, random, setter, next, state);
                    }
                    return next;
                };
            }
        }
        return module;
    }

    private MethodAndParameters buildMethodWithParameters(Method[] methods) {
        val setters = new MethodWithParameter[methods.length];
        var idx = 0;
        for (val method : methods) {
            val name = method.getName();
            if (name.length() > 3 && name.startsWith("set") && Character.isUpperCase(name.charAt(3))) {
                val returnType = method.getReturnType();
                if (returnType == void.class) {
                    val parameters = method.getParameters();
                    if (parameters.length == 1) {
                        val parameter = parameters[0];
                        val maybeParameterizedType = parameter.getParameterizedType();
                        if (maybeParameterizedType instanceof ParameterizedType) {
                            val parameterizedType = (ParameterizedType) maybeParameterizedType;
                            setters[idx++] = new MethodWithParameter(method, parameter.getType(), null);
                        } else {
                            setters[idx++] = new MethodWithParameter(method, parameter.getType(), null);
                        }
                    }
                }
            }
        }
        val settersDest = new MethodWithParameter[idx];
        System.arraycopy(setters, 0, settersDest, 0, idx);
        return new MethodAndParameters(
                Arrays.asList(settersDest)
        );
    }

    @SneakyThrows
    private Object newInstanceNoParams(Constructor<?> constructor) {
        return constructor.newInstance();
    }

    @SneakyThrows
    private Object newInstanceUnknownType(RNGesis rnGesis,
                                          Random random,
                                          RNGesisModule<?>[] childModules,
                                          Constructor<?> constructor,
                                          NewModuleState state) {
        val parameters = new Object[childModules.length];
        for (var i = 0; i < parameters.length; i++) {
            val childModule = childModules[i];
            parameters[i] = childModule.next(rnGesis, random, state);
        }
        return constructor.newInstance(parameters);
    }


    @SneakyThrows
    private void invokeSetter(RNGesis rnGesis,
                              Random random,
                              MethodWithParameter setter,
                              Object object,
                              NewModuleState state) {
        setter.getMethod().invoke(
                object,
                RNGUnknownTypes.internalNextObject(
                        parent,
                        rnGesis,
                        setter.getParameterType(),
                        random,
                        state
                )
        );
    }
}
