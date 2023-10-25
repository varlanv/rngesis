package io.rngesis.internal;

import io.rngesis.api.RNGesisModule;
import lombok.val;
import lombok.var;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class RNGModules {

    private static final ConcurrentHashMap<String, RNGesisModule<?>> moduleMap = new ConcurrentHashMap<>(RNGDefaultModules.all());
    private static final ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    public RNGesisModule<?> getOrCompute(String typeName, Supplier<RNGesisModule<?>> supplier) {
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
        moduleMap.putAll(RNGDefaultModules.all());
        lockMap.clear();
    }

    private static Lock getLockForKey(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
