package io.rngesis.internal;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ThreadScopeRandom {

    private final ConcurrentMap<Long, Random> threadLocalRandom;
    private final Supplier<Random> randomSupplier;

    public ThreadScopeRandom(Supplier<Random> randomSupplier) {
        this.threadLocalRandom = new ConcurrentHashMap<>();
        this.randomSupplier = randomSupplier;
    }

    public Random get() {
        val key = Thread.currentThread().getId();
        var random = threadLocalRandom.get(key);
        if (random == null) {
            random = randomSupplier.get();
            threadLocalRandom.put(key, random);
        }
        return random;
    }
}
