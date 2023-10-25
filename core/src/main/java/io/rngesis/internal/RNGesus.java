package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.val;
import lombok.var;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class RNGesus implements RNGesis {

    private final ThreadScopeRandom threadScopeRandom;
    private final RNGUnknownTypes rngUnknownType;
    private final RNGDefaultTypes rngDefaultTypes;

    public RNGesus(Supplier<Random> random) {
        this.threadScopeRandom = new ThreadScopeRandom(random);
        this.rngDefaultTypes = new RNGDefaultTypes();
        this.rngUnknownType = new RNGUnknownTypes();
    }

    public RNGesus(Random random) {
        this(() -> random);
    }

    public RNGesus() {
        this(Random::new);
    }

    @Override
    public <T> T nextObject(Class<T> type) {
        return rngUnknownType.nextObject(this, type, random());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> nextObjects(Class<T> type, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        val array = (T[]) new Object[size];
        for (var i = 0; i < size; i++) {
            array[i] = nextObject(type);
        }
        return Arrays.asList(array);
    }

    @Override
    public Integer nextInt() {
        return rngDefaultTypes.nextInt(random());
    }

    @Override
    public Long nextLong() {
        return rngDefaultTypes.nextLong(random());
    }

    @Override
    public String nextString() {
        return rngDefaultTypes.nextString(random());
    }

    @Override
    public <T> RNGesis withModule(RNGesisModule<T> module) {
        return this;
    }

    public Random random() {
        return threadScopeRandom.get();
    }
}
