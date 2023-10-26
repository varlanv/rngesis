package io.rngesis.internal;

import io.rngesis.api.RNGesisModule;
import lombok.val;
import lombok.var;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class RNGModules {

    private static final Map<String, RNGesisModule<?>> moduleMap = new ConcurrentHashMap<>();
    private static final Map<String, RNGesisModule<?>> defaultModuleMap = RNGDefaultModules.all();
    private static final ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    public RNGesisModule<?> getDefault(String typeName) {
        return Objects.requireNonNull(defaultModuleMap.get(typeName));
    }

    public RNGesisModule<?> getOrCompute(String typeName, Supplier<RNGesisModule<?>> supplier) {
        return getOrComputeInternal(
                typeName,
                supplier
        );
    }

    public RNGesisModule<?> getOrComputeInternal(String typeName, Supplier<RNGesisModule<?>> supplier) {
        RNGesisModule<?> dfrngmodule = defaultModuleMap.get(typeName);
        if (dfrngmodule != null) {
            return dfrngmodule;
        }
        var rnGesisModule = moduleMap.get(typeName);
        if (rnGesisModule == null) {
            val lock = getLockForKey(typeName);
            lock.lock();
            try {
                rnGesisModule = moduleMap.get(typeName);
                if (rnGesisModule == null) {
                    rnGesisModule = supplier.get();
                    moduleMap.put(typeName, rnGesisModule);
                }
            } finally {
                lock.unlock();
            }
        }
        return rnGesisModule;
    }

    static synchronized void reset() {
        moduleMap.clear();
        lockMap.clear();
    }

    private Lock getLockForKey(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
