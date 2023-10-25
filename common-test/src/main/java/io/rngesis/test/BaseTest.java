package io.rngesis.test;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseTest {

    public static final int DEFAULT_REPEAT_COUNT = 10;

    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    static AtomicInteger cnt = new AtomicInteger(0);

    @SneakyThrows
    public void parallel(int nThreads, ThrowingRunnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        CountDownLatch readyToStartLock = new CountDownLatch(nThreads);
        CountDownLatch startLock = new CountDownLatch(1);
        CountDownLatch finishedLock = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            executorService.submit(() -> {
                try {
                    readyToStartLock.countDown();
                    startLock.await(5, TimeUnit.SECONDS);
                    runnable.run();
                    finishedLock.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        readyToStartLock.await(5, TimeUnit.SECONDS);
        startLock.countDown();
        finishedLock.await(5, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

    @SneakyThrows
    public void parallel(ThrowingRunnable runnable) {
        parallel(DEFAULT_REPEAT_COUNT, runnable);
    }

    @SneakyThrows
    public void nonParallel(int iterations, ThrowingRunnable runnable) {
        for (int i = 0; i < iterations; i++) {
            runnable.run();
        }
    }

    @SneakyThrows
    public void nonParallel(ThrowingRunnable runnable) {
        nonParallel(DEFAULT_REPEAT_COUNT, runnable);
    }
}
