package io.rngesis.internal;

import io.rngesis.api.RNGesisModule;
import io.rngesis.test.BaseStatelessUnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Isolated
class RNGModulesTest extends BaseStatelessUnitTest {

    private final String type = "some_type";
    private final String moduleCallResult = "some_result";

    @AfterEach
    void cleanup() {
        RNGModules.reset();
    }

    @Test
    void getOrCompute_if_module_not_exists_should_create_and_return_module() {
        AtomicInteger counter = new AtomicInteger();
        RNGesisModule<Object> objectRNGesisModule = (rngesis, random, state) -> moduleCallResult;
        Supplier<RNGesisModule<?>> supplier = () -> {
            counter.incrementAndGet();
            return objectRNGesisModule;
        };
        RNGModules subject = new RNGModules();

        RNGesisModule<?> actual = subject.getOrCompute(type, supplier);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isEqualTo(objectRNGesisModule);
        Assertions.assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    void getOrCompute_if_module_exists_should_return_module_and_not_call_supplier() {
        AtomicInteger counter = new AtomicInteger();
        RNGesisModule<Object> objectRNGesisModule = (rngesis, random, state) -> moduleCallResult;
        Supplier<RNGesisModule<?>> supplier = () -> {
            counter.incrementAndGet();
            return objectRNGesisModule;
        };
        RNGModules subject = new RNGModules();

        subject.getOrCompute(type, supplier);
        subject.getOrCompute(type, supplier);
        RNGesisModule<?> actual = subject.getOrCompute(type, supplier);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isEqualTo(objectRNGesisModule);
        Assertions.assertThat(counter.get()).isEqualTo(1);
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void getOrCompute_if_multiple_threads_request_same_type_then_only_one_should_be_created() {
        AtomicInteger counter = new AtomicInteger();
        RNGesisModule<Object> objectRNGesisModule = (rngesis, random, state) -> moduleCallResult;
        Supplier<RNGesisModule<?>> supplier = () -> {
            counter.incrementAndGet();
            return objectRNGesisModule;
        };
        Collection<RNGesisModule<?>> modules = new ConcurrentLinkedQueue<>();
        RNGModules subject = new RNGModules();
        int threads = 20;

        parallel(threads, () -> modules.add(subject.getOrCompute(type, supplier)));

        Assertions.assertThat(modules).hasSize(threads);
        Assertions.assertThat(modules).allMatch(m -> m == objectRNGesisModule);
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void getOrCompute_if_multiple_threads_request_same_type_from_different_instances_then_only_one_should_be_created() {
        AtomicInteger counter = new AtomicInteger();
        Collection<RNGesisModule<?>> modules = new ConcurrentLinkedQueue<>();
        RNGesisModule<Object> objectRNGesisModule = (rngesis, random, state) -> moduleCallResult;
        int threads = 20;

        parallel(threads, () -> {
            Supplier<RNGesisModule<?>> supplier = () -> {
                counter.incrementAndGet();
                return objectRNGesisModule;
            };
            RNGModules subject = new RNGModules();
            modules.add(subject.getOrCompute(type, supplier));
        });

        Assertions.assertThat(counter.get()).isEqualTo(1);
        Assertions.assertThat(modules).hasSize(threads);
        Assertions.assertThat(modules).allMatch(m -> m == objectRNGesisModule);
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void getOrCompute_if_multiple_threads_request_different_types_from_different_instances_then_each_should_be_created_only_once() {
        AtomicInteger counter = new AtomicInteger();
        Collection<RNGesisModule<?>> modules = new ConcurrentLinkedQueue<>();
        RNGesisModule<Object> objectRNGesisModule = (rngesis, random, state) -> moduleCallResult;
        int threads = 20;
        Queue<String> types = IntStream.range(0, threads)
                .mapToObj(i -> i % 2 == 0 ? "type_1" : "type_2")
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

        parallel(threads, () -> {
            Supplier<RNGesisModule<?>> supplier = () -> {
                counter.incrementAndGet();
                return objectRNGesisModule;
            };
            RNGModules subject = new RNGModules();
            modules.add(subject.getOrCompute(types.poll(), supplier));
        });

        Assertions.assertThat(counter.get()).isEqualTo(2);
        Assertions.assertThat(modules).hasSize(threads);
        Assertions.assertThat(modules).allMatch(m -> m == objectRNGesisModule);
    }
}
