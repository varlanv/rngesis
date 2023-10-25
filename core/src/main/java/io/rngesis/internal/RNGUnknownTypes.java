package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.SneakyThrows;
import lombok.val;

import java.util.List;
import java.util.Random;

public class RNGUnknownTypes {

    static final RNGModules modules = new RNGModules();

    public <T> T nextObject(RNGesis rnGesis,
                            Class<T> type,
                            Random random) {
        return internalNextObject(
                this,
                rnGesis,
                type,
                random,
                null
        );
    }

    @SuppressWarnings("unchecked")
    static <T> T internalNextObject(RNGUnknownTypes parent,
                                    RNGesis rnGesis,
                                    Class<T> type,
                                    Random random,
                                    NewModuleState state) {
        val typeName = type.getName();
        val rnGesisModule = modules.getOrCompute(
                typeName,
                () -> computeNewModule(
                        parent,
                        typeName,
                        rnGesis,
                        type,
                        random,
                        state
                )
        );
        if (rnGesisModule.isStateless()) {
            return (T) rnGesisModule.next(rnGesis, random, null);
        } else {
            val objectNewModuleCreateOperation = state == null ?
                    new NewModuleState(rnGesis, type, typeName, random) :
                    state;
            return (T) rnGesisModule.next(rnGesis, random, objectNewModuleCreateOperation);
        }
    }

    @SneakyThrows
    static <T> RNGesisModule<?> computeNewModule(RNGUnknownTypes parent,
                                                 String typeName,
                                                 RNGesis rnGesis,
                                                 Class<T> type,
                                                 Random random,
                                                 NewModuleState state) {
        if (type.isEnum()) {
            return enumModule(type);
        }
        if (type.isAssignableFrom(List.class) && state != null) {
            System.out.println();
        }
        return new RnNewModule<>(parent, typeName, rnGesis, type, random, state).computeNewModule();
    }

    private static <T> RNGesisModule<?> enumModule(Class<T> type) {
        val enumConstants = type.getEnumConstants();
        if (enumConstants.length == 0) {
            return new RNGStatelessModule<>((rnGesis, random) -> null);
        } else {
            return new RNGStatelessModule<>((rnGesis, random) -> enumConstants[random.nextInt(enumConstants.length)]);
        }
    }
}
