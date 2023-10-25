package io.rngesis.internal;

import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class ThreadScopeRandom {

    public Random get() {
        return ThreadLocalRandom.current();
    }
}
