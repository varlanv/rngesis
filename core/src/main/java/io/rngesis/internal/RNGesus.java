package io.rngesis.internal;

import io.rngesis.api.RNGesis;
import io.rngesis.api.RNGesisModule;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RNGesus implements RNGesis {

    private final ThreadScopeRandom threadScopeRandom;
    private final RNGUnknownTypes rngUnknownType;
    private final RNGDefaultTypes rngDefaultTypes;

    public RNGesus() {
        this.threadScopeRandom = new ThreadScopeRandom();
        this.rngDefaultTypes = new RNGDefaultTypes();
        this.rngUnknownType = new RNGUnknownTypes();
    }

    @Override
    public <T> T nextObject(Class<T> type) {
        return rngUnknownType.nextObject(this, type, random());
    }

    @Override
    public <T> List<T> nextObjects(Class<T> type, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        val list = new ArrayList<T>(size);
        for (var i = 0; i < size; i++) {
            list.add(nextObject(type));
        }
        return list;
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
