package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import lombok.var;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Value
@SuppressWarnings({"unchecked", "rawtypes"})
public class RnNewModule<T> {

    RNGUnknownTypes parent;
    String typeName;
    RNGesis rnGesis;
    Class<T> type;
    Random random;
    NewModuleState state;


    @SneakyThrows
    public RNGesisModule<?> computeNewModule() {
        val module = getParameterizedRnGesisModule(type);
        if (module != null) {
            return wrapSetters(type, module);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type.getName());
        }
    }

    private RNGesisModuleWithConstructorParams getParameterizedRnGesisModule(Class<T> type) {
        val constructors = type.getConstructors();
        if (constructors.length == 1) {
            val constructor = constructors[0];
            int modifiers = constructor.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                val parameterTypes = constructor.getGenericParameterTypes();
                if (parameterTypes.length == 0) {
                    return new RNGesisModuleWithConstructorParams((rnGesis, random, state) -> newInstanceNoParams(constructor));
                } else {
                    val childModules = prepareChildModules(parameterTypes);
                    val set = parameterNames(constructor);
                    return new RNGesisModuleWithConstructorParams(
                            (rnGesis, random, state) -> newInstanceUnknownType(
                                    rnGesis,
                                    random,
                                    childModules,
                                    constructor,
                                    state
                            ),
                            set
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
                    return new RNGesisModuleWithConstructorParams(
                            (rnGesis, random, state) -> newInstanceNoParams(fc)
                    );
                } else {
                    val childModules = prepareChildModules(fc.getGenericParameterTypes());
                    val set = parameterNames(fc);
                    return new RNGesisModuleWithConstructorParams(
                            (rnGesis, random, state) -> newInstanceUnknownType(
                                    rnGesis,
                                    random,
                                    childModules,
                                    fc,
                                    state
                            ),
                            set
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

    private HashSet<String> parameterNames(Constructor<?> constructor) {
        val parameters = constructor.getParameters();
        val set = new HashSet<String>(parameters.length);
        for (val parameter : parameters) {
            set.add(parameter.getName());
        }
        return set;
    }

    private RNGesisModule<?>[] prepareChildModules(Type[] parameterTypes) {
        val childModules = new RNGesisModule<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            val param = parameterTypes[i];
            val rawCollectionTypeModule = rawCollectionTypeModule(param);
            if (rawCollectionTypeModule != null) {
                childModules[i] = rawCollectionTypeModule;
                continue;
            }
            if (param instanceof ParameterizedType) {
                RNGesisModule<?> module = getParameterizedRnGesisModule((ParameterizedType) param);
                if (module != null) {
                    childModules[i] = module;
                } else {
                    throw new IllegalStateException(String.format("Could not instantiate nested param %s of %s",
                            param.getTypeName(), typeName));
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

    private RNGesisModule<?> rawCollectionTypeModule(Type unknownType) {
        if (unknownType instanceof Class) {
            val type = (Class<?>) unknownType;
            if (Iterable.class.isAssignableFrom(type)) {
                if (type == List.class || type == Iterable.class || type == Collection.class || type == ArrayList.class) {
                    return (rnGesis, random, state) -> new ArrayList<>();
                } else if (type == Set.class || type == HashSet.class) {
                    return (rnGesis, random, state) -> new HashSet<>();
                } else if (type == Queue.class || type == LinkedList.class) {
                    return (rnGesis, random, state) -> new LinkedList<>();
                } else if (type == Deque.class || type == ArrayDeque.class) {
                    return (rnGesis, random, state) -> new ArrayDeque<>();
                } else if (type == SortedSet.class || type == TreeSet.class || type == NavigableSet.class) {
                    return (rnGesis, random, state) -> new TreeSet<>();
                }
            } else if (Map.class.isAssignableFrom(type)) {
                if (type == Map.class || type == HashMap.class) {
                    return (rnGesis, random, state) -> new HashMap<>();
                } else if (type == SortedMap.class || type == TreeMap.class || type == NavigableMap.class) {
                    return (rnGesis, random, state) -> new TreeMap<>();
                } else if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
                    return (rnGesis, random, state) -> new ConcurrentHashMap<>();
                } else if (type == ConcurrentNavigableMap.class || type == ConcurrentSkipListMap.class) {
                    return (rnGesis, random, state) -> new ConcurrentSkipListMap<>();
                }
            }
        }
        return null;
    }

    private RNGesisModule<?> getParameterizedRnGesisModule(ParameterizedType param) {
        val rawType = (Class<?>) param.getRawType();
        if (Iterable.class.isAssignableFrom(rawType)) {
            if (rawType == List.class ||
                    rawType == Iterable.class ||
                    rawType == Collection.class ||
                    rawType == ArrayList.class ||
                    ArrayList.class.isAssignableFrom(rawType)) {
                return getCollectionModule(
                        param,
                        rawType,
                        ArrayList::new,
                        (objects, next) -> ((ArrayList) objects).add(next)
                );
            } else if (rawType == Set.class || rawType == HashSet.class) {
                return getCollectionModule(
                        param,
                        rawType,
                        HashSet::new,
                        (objects, next) -> ((HashSet) objects).add(next)
                );
            } else if (rawType == Queue.class || rawType == LinkedList.class) {
                return getCollectionModule(
                        param,
                        rawType,
                        size -> new LinkedList<>(),
                        (objects, next) -> ((LinkedList) objects).add(next)
                );
            } else if (rawType == Deque.class || rawType == ArrayDeque.class) {
                return getCollectionModule(
                        param,
                        rawType,
                        ArrayDeque::new,
                        (objects, next) -> ((ArrayDeque) objects).add(next)
                );
            } else if (rawType == SortedSet.class || rawType == TreeSet.class || rawType == NavigableSet.class) {
                return getCollectionModule(
                        param,
                        rawType,
                        size -> new TreeSet<>(),
                        (objects, next) -> ((TreeSet) objects).add(next)
                );
            }
        } else if (rawType == Optional.class) {
            return getOptionalModule(param, rawType);
        } else if (Map.class.isAssignableFrom(rawType)) {
            val actualTypeArguments = param.getActualTypeArguments();
            if (isWildcardParams(actualTypeArguments)) {
                return getEmptyMapModule(rawType);
            } else {
                val keyType = actualTypeArguments[0];
                val valueType = actualTypeArguments[1];

                val keyModule = getModuleForType(keyType);
                val valueModule = getModuleForType(valueType);
                val typeNameKey = rawType + "<" + keyType.getTypeName() + "," + valueType.getTypeName() + ">";
                if (rawType == Map.class || rawType == HashMap.class) {
                    if (((Class<?>) keyType).isEnum()) {
                        return createMapModule(
                                typeNameKey,
                                keyModule,
                                valueModule,
                                size -> new EnumMap((Class<?>) keyType),
                                Map::put
                        );
                    } else {
                        return createMapModule(
                                typeNameKey,
                                keyModule,
                                valueModule,
                                HashMap::new,
                                Map::put
                        );
                    }
                } else if (rawType == SortedMap.class || rawType == TreeMap.class || rawType == NavigableMap.class) {
                    return createMapModule(
                            typeNameKey,
                            keyModule,
                            valueModule,
                            size -> new TreeMap<>(),
                            Map::put
                    );
                } else if (rawType == ConcurrentMap.class || rawType == ConcurrentHashMap.class) {
                    return createMapModule(
                            typeNameKey,
                            keyModule,
                            valueModule,
                            ConcurrentHashMap::new,
                            Map::put
                    );
                } else if (rawType == ConcurrentNavigableMap.class || rawType == ConcurrentSkipListMap.class) {
                    return createMapModule(
                            typeNameKey,
                            keyModule,
                            valueModule,
                            size -> new ConcurrentSkipListMap<>(),
                            Map::put
                    );
                }
            }
        }
        return null;
    }

    private RNGesisModule<?> getModuleForType(Type type) {
        if (type instanceof ParameterizedType) {
            return getParameterizedRnGesisModule((ParameterizedType) type);
        } else if (type instanceof WildcardType) {
            throw new RuntimeException("?");
        } else if (type instanceof Class) {
            Class<?> classType = (Class<?>) type;
            return RNGUnknownTypes.modules.getOrCompute(
                    classType.getName(),
                    () -> RNGUnknownTypes.computeNewModule(
                            parent,
                            classType.getName(),
                            rnGesis,
                            classType,
                            random,
                            state
                    )
            );
        } else {
            throw new IllegalArgumentException("Unexpected type: " + type.getTypeName());
        }
    }

    private RNGesisModule<?> createMapModule(String typeKeyName,
                                             RNGesisModule<?> keyModule,
                                             RNGesisModule<?> valueModule,
                                             Function<Integer, Map<Object, Object>> mapFn,
                                             TriConsumer<Map<Object, Object>, Object, Object> mapKeyValConsumer) {
        return RNGUnknownTypes.modules.getOrCompute(
                typeKeyName,
                () -> (rnGesis, random, state) -> {
                    val size = random.nextInt(3) + 1;
                    val map = mapFn.apply(size);
                    for (var i = 0; i < size; i++) {
                        val key = keyModule.next(rnGesis, random, state);
                        val value = valueModule.next(rnGesis, random, state);
                        mapKeyValConsumer.accept(map, key, value);
                    }
                    return map;
                }
        );
    }

    private RNGesisModule<?> getEmptyMapModule(Class<?> rawType) {
        if (rawType == Map.class || rawType == HashMap.class) {
            return (rnGesis, random, state) -> new HashMap<>();
        } else if (rawType == SortedMap.class || rawType == TreeMap.class || rawType == NavigableMap.class) {
            return (rnGesis, random, state) -> new TreeMap<>();
        } else if (rawType == ConcurrentMap.class || rawType == ConcurrentHashMap.class) {
            return (rnGesis, random, state) -> new ConcurrentHashMap<>();
        } else if (rawType == ConcurrentNavigableMap.class || rawType == ConcurrentSkipListMap.class) {
            return (rnGesis, random, state) -> new ConcurrentSkipListMap<>();
        }
        return null;
    }

    private boolean isWildcardParams(Type[] actualTypeArguments) {
        return actualTypeArguments[0] instanceof WildcardType || actualTypeArguments[1] instanceof WildcardType;
    }

    private RNGesisModule<?> getCollectionModule(ParameterizedType param,
                                                 Class<?> rawType,
                                                 Function<Integer, Object> collectionSupplier,
                                                 BiConsumer<Object, Object> instanceConsumer) {
        val nestedActualTypeArgument = param.getActualTypeArguments()[0];
        if (nestedActualTypeArgument instanceof ParameterizedType) {
            val nestedModule = getParameterizedRnGesisModule((ParameterizedType) nestedActualTypeArgument);
            return createCollectionModule(
                    rawType.getName(),
                    nestedModule,
                    nestedActualTypeArgument.getTypeName(),
                    collectionSupplier,
                    instanceConsumer
            );
        } else if (nestedActualTypeArgument instanceof WildcardType) {
            return RNGUnknownTypes.modules.getOrCompute(
                    rawType.getName() + "<?>",
                    () -> (rnGesis, random, state) -> Collections.emptyList());
        } else {
            val actualTypeArgument = (Class<?>) nestedActualTypeArgument;
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
            return createCollectionModule(
                    rawType.getName(),
                    actualTypeModule,
                    actualTypeArgumentName,
                    collectionSupplier,
                    instanceConsumer
            );
        }
    }

    private RNGesisModule<?> getOptionalModule(ParameterizedType param,
                                               Class<?> rawType) {
        val nestedActualTypeArgument = param.getActualTypeArguments()[0];
        if (nestedActualTypeArgument instanceof ParameterizedType) {
            val nestedModule = getParameterizedRnGesisModule((ParameterizedType) nestedActualTypeArgument);
            return createOptionalModule(
                    rawType.getName(),
                    nestedModule,
                    nestedActualTypeArgument.getTypeName()
            );
        } else if (nestedActualTypeArgument instanceof WildcardType) {
            return RNGUnknownTypes.modules.getOrCompute(
                    rawType.getName() + "<?>",
                    () -> (rnGesis, random, state) -> Collections.emptyList());
        } else {
            val actualTypeArgument = (Class<?>) nestedActualTypeArgument;
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
            return createOptionalModule(
                    rawType.getName(),
                    actualTypeModule,
                    actualTypeArgumentName
            );
        }
    }

    private RNGesisModule<?> createCollectionModule(String rawTypeName,
                                                    RNGesisModule<?> innerModule,
                                                    String innerTypeName,
                                                    Function<Integer, Object> collectionSupplier,
                                                    BiConsumer<Object, Object> instanceConsumer) {
        val moduleName = rawTypeName + "<" + innerTypeName + ">";
        return RNGUnknownTypes.modules.getOrCompute(
                moduleName,
                () -> (rnGesis, random, state) -> {
                    val size = random.nextInt(3) + 1;
                    val objects = collectionSupplier.apply(size);
                    for (var i1 = 0; i1 < size; i1++) {
                        val next = innerModule.next(rnGesis, random, state);
                        instanceConsumer.accept(objects, next);
                    }
                    return objects;
                }
        );
    }

    private RNGesisModule<?> createOptionalModule(String rawTypeName,
                                                  RNGesisModule<?> innerModule,
                                                  String innerTypeName) {
        val moduleName = rawTypeName + "<" + innerTypeName + ">";
        return RNGUnknownTypes.modules.getOrCompute(
                moduleName,
                () -> (rnGesis, random, state) -> Optional.of(innerModule.next(rnGesis, random, state))
        );
    }

    private RNGesisModule<?> wrapSetters(Class<T> type,
                                         RNGesisModuleWithConstructorParams module) {
        val methods = type.getMethods();
        if (methods.length > 0) {
            val setters = buildMethodWithParameters(methods, module);
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

    private MethodAndParameters buildMethodWithParameters(Method[] methods,
                                                          RNGesisModuleWithConstructorParams module) {
        val setters = new MethodWithParameter[methods.length];
        var idx = 0;
        val constructorParamNames = module.getParamNames();
        for (val method : methods) {
            val methodName = method.getName();
            if (methodName.length() > 3 && methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3))) {
                val returnType = method.getReturnType();
                if (returnType == void.class) {
                    val parameters = method.getParameters();
                    if (parameters.length == 1) {
                        val parameter = parameters[0];
                        if (!constructorParamNames.contains(parameter.getName())) {
                            val maybeParameterizedType = parameter.getParameterizedType();
                            if (maybeParameterizedType instanceof ParameterizedType) {
//                                val parameterizedType = (ParameterizedType) maybeParameterizedType;
                                setters[idx++] = new MethodWithParameter(method, null, parameter.getType(), null);
                            } else {
                                setters[idx++] = new MethodWithParameter(method, null, parameter.getType(), null);
                            }
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
