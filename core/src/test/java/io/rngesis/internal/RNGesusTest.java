package io.rngesis.internal;

import io.rngesis.internal.types.*;
import io.rngesis.test.BaseStatelessUnitTest;
import lombok.val;
import lombok.var;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
class RNGesusTest extends BaseStatelessUnitTest {

    private static final RNGesus subject = new RNGesus();

    @Test
    void nextInt_should_generate_random_int() {
        Assertions.assertThat(subject.nextInt()).isNotNull();
    }

    @Test
    void nextLong_should_generate_random_long() {
        Assertions.assertThat(subject.nextLong()).isNotNull();
    }

    @Test
    void nextString_should_generate_random_string() {
        Assertions.assertThat(subject.nextString()).isNotNull();
    }

    @Test
    void nextString_random_string_should_not_be_empty_or_blank() {
        Assertions.assertThat(subject.nextString()).isNotBlank();
    }

    @Test
    void nextObject_should_generate_random_string() {
        Assertions.assertThat(subject.nextObject(String.class)).isNotNull();
    }

    @Test
    void nextObject_should_generate_random_string_not_blank() {
        Assertions.assertThat(subject.nextObject(String.class)).isNotBlank();
    }

    @Test
    void nextObject_should_generate_random_int_primitive() {
        Assertions.assertThat(subject.nextObject(int.class)).isNotZero();
    }

    @Test
    void nextObject_should_generate_random_int_object() {
        Assertions.assertThat(subject.nextObject(Integer.class)).isNotNull().isNotZero();
    }

    @Test
    void nextObject_should_generate_date() {
        Date actual = subject.nextObject(Date.class);

        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    void nextObject_should_generate_OffsetDateTime() {
        val actual = subject.nextObject(OffsetDateTime.class);

        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    void nextObject_should_generate_random_long_object() {
        Assertions.assertThat(subject.nextObject(Long.class)).isNotNull().isNotZero();
    }

    @Test
    void nextObject_should_generate_random_long_primitive() {
        Assertions.assertThat(subject.nextObject(long.class)).isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_string_and_integer_constructor_should_construct_object() {
        val actual = subject.nextObject(StringAndIntegerConstructorType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getString()).isNotBlank();
        Assertions.assertThat(actual.getInteger()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_string_and_integer_setters_should_construct_object() {
        val actual = subject.nextObject(StringAndIntegerSetterType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getString()).isNotBlank();
        Assertions.assertThat(actual.getInteger()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_string_and_integer_setters_and_constructor_should_construct_object() {
        val actual = subject.nextObject(StringAndIntegerSetterAndConstructorType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getStringConstructor()).isNotBlank();
        Assertions.assertThat(actual.getIntegerConstructor()).isNotNull().isNotZero();
        Assertions.assertThat(actual.getStringSetter()).isNotBlank();
        Assertions.assertThat(actual.getIntegerSetter()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_enum_without_values_should_return_null() {
        val actual = subject.nextObject(EnumTypeNoValues.class);

        Assertions.assertThat(actual).isNull();
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void nextObject_unknown_type_enum_with_value_should_return_it() {
        val actual = subject.nextObject(EnumTypeOneValue.class);

        Assertions.assertThat(actual).isEqualTo(EnumTypeOneValue.VALUE);
    }

    @Test
    void nextObject_unknown_type_enum_with_20_values_should_return_random_value() {
        val actual = subject.nextObject(EnumType20Values.class);

        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    void nextObject_unknown_type_enum_with_20_values_should_when_called_multiple_times_return_random_value() {
        val collection = new ConcurrentLinkedQueue<>();
        int threads = 20;

        parallel(threads, () -> collection.add(subject.nextObject(EnumType20Values.class)));

        Assertions.assertThat(collection).hasSize(threads);
        Assertions.assertThat(new HashSet<>(collection)).hasSizeGreaterThan(1);
    }

    @Test
    void nextObject_unknown_type_when_string_and_integer_setters_and_two_constructors_should_use_constructor_with_highest_params_count_and_construct_object() {
        val actual = subject.nextObject(StringAndIntegerSetterAndTwoConstructorType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getStringSetter()).isNotBlank();
        Assertions.assertThat(actual.getIntegerSetter()).isNotNull().isNotZero();
        Assertions.assertThat(actual.getStringConstructor()).isNotBlank();
        Assertions.assertThat(actual.getIntegerConstructor()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_string_and_integer_constructor_and_created_two_instances_should_return_different_instances() {
        val actual1 = subject.nextObject(StringAndIntegerConstructorType.class);
        val actual2 = subject.nextObject(StringAndIntegerConstructorType.class);

        Assertions.assertThat(actual1).isNotSameAs(actual2);
        Assertions.assertThat(actual1.getString()).isNotEqualTo(actual2.getString());
        Assertions.assertThat(actual1.getInteger()).isNotEqualTo(actual2.getInteger());
    }

    @Test
    void nextObject_unknown_type_when_list_with_bigint_generic_then_should_create_object() {
        val actual = subject.nextObject(ListWithBigIntegerGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getList()).isNotNull();
        Assertions.assertThat(actual.getList()).hasSizeBetween(1, 3);
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void nextObject_unknown_type_when_list_with_bigint_generic_created_100_instances_then_should_have_different_sizes() {
        val sizes = new HashSet<>();

        for (var i = 0; i < 100; i++) {
            sizes.add(subject.nextObject(ListWithBigIntegerGenericType.class).getList().size());
        }

        Assertions.assertThat(sizes).containsOnly(1, 2, 3);
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void nextObject_unknown_type_when_list_with_bigint_generic_created_100_instances_then_should_have_different_values() {
        val values = new HashSet<>();
        val count = 100;
        val valuesCount = new AtomicInteger();

        for (var i = 0; i < count; i++) {
            subject.nextObject(ListWithBigIntegerGenericType.class).getList().forEach(bigint -> {
                values.add(bigint);
                valuesCount.getAndIncrement();
            });
        }

        Assertions.assertThat(values).hasSize(valuesCount.get());
        Assertions.assertThat(values).doesNotContainNull();
    }

    @RepeatedTest(DEFAULT_REPEAT_COUNT)
    void nextObject_unknown_type_when_string_and_integer_constructor_and_creating_in_parallel_then_instances_should_be_different() {
        val collection = new ConcurrentLinkedQueue<>();
        val nThreads = 10;

        parallel(nThreads, () -> collection.add(subject.nextObject(StringAndIntegerConstructorType.class)));

        Assertions.assertThat(collection).hasSize(nThreads);
        Assertions.assertThat(collection).doesNotContainNull();
        Assertions.assertThat(collection).doesNotHaveDuplicates();
    }

    @Test
    void nextObject_unknown_type_when_empty_type_should_construct_object() {
        val actual = subject.nextObject(EmptyType.class);

        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    void nextObject_unknown_type_when_created_twice_should_return_different_instances() {
        val actual1 = subject.nextObject(EmptyType.class);
        val actual2 = subject.nextObject(EmptyType.class);

        Assertions.assertThat(actual1).isNotSameAs(actual2);
    }

    @Test
    void nextObjects_should_generate_list_of_objects_from_primitive_long() {
        val size = 2;
        val actual = subject.nextObjects(long.class, size);

        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual).hasSize(size);
        Assertions.assertThat(actual).doesNotContainNull();
        Assertions.assertThat(actual).extracting(Long::longValue).doesNotContain(0L);
    }

    @Test
    void nextObjects_should_generate_list_of_objects_from_object_long() {
        val size = 2;
        val actual = subject.nextObjects(Long.class, size);

        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual).hasSize(size);
        Assertions.assertThat(actual).doesNotContainNull();
        Assertions.assertThat(actual).extracting(Long::longValue).doesNotContain(0L);
    }

    @Test
    void nextObjects_if_size_is_eq_zero_should_throw_exception() {
        val size = 0;

        Assertions.assertThatThrownBy(() -> subject.nextObjects(Long.class, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Size must be greater than 0");
    }

    @Test
    void nextObjects_if_size_is_lt_zero_should_throw_exception() {
        val size = -1;

        Assertions.assertThatThrownBy(() -> subject.nextObjects(Long.class, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Size must be greater than 0");
    }

    @RepeatedTest(BaseStatelessUnitTest.DEFAULT_REPEAT_COUNT)
    void nextInt_whenRandomizedInParallel_shouldGiveRandomValues() throws Exception {
        val nIterations = 1000;
        val ints = new ConcurrentHashMap<>();
        parallel(() -> {
            for (int j = 0; j < nIterations; j++) {
                ints.put(subject.nextInt(), 0);
            }
        });

        Assertions.assertThat(ints.size()).isGreaterThan((DEFAULT_REPEAT_COUNT * nIterations) - 10);
    }

    @Test
    void nextInt_whenRandomizedInParallel_shouldNotContainNulls() throws Exception {
        val nIterations = 1000;
        val ints = new ConcurrentLinkedQueue<>();
        parallel(() -> {
            for (int j = 0; j < nIterations; j++) {
                ints.add(new int[]{subject.nextInt()});
            }
        });

        Assertions.assertThat(ints).doesNotContainNull();
    }

    static List<Long> times = new ArrayList<>();

    @RepeatedTest(1)
//    @Disabled
    void pa() throws Exception {
        int iterations = 2000000;
        StringAndIntegerConstructorType[] ints = new StringAndIntegerConstructorType[iterations];
        final int[] cnt = {0};
        long fullBefore = System.currentTimeMillis();
//        EasyRandom easyRandom = new EasyRandom();
        RNGesus easyRandom = new RNGesus();
//        Random easyRandom = new Random();
        Constructor<StringAndIntegerConstructorType> constructor = StringAndIntegerConstructorType.class.getConstructor(String.class, Integer.class);
        easyRandom.nextObject(StringAndIntegerConstructorType.class);
        long iterationBefore = System.currentTimeMillis();
        nonParallel(iterations, () -> {
//            Rnd rnd = new Rnd(String.valueOf(easyRandom.nextObject(String.class)), easyRandom.nextInt());
//            Rnd rnd = constructor.newInstance(String.valueOf(easyRandom.nextObject(String.class)), easyRandom.nextInt());
//            StringAndIntegerConstructorType rnd = constructor.newInstance("1", 2);
//            StringAndIntegerConstructorType rnd = constructor.newInstance(easyRandom.nextObject(String.class), easyRandom.nextInt());
//            StringAndIntegerConstructorType rnd = constructor.newInstance(String.valueOf(easyRandom.nextInt()), easyRandom.nextInt());
            StringAndIntegerConstructorType rnd = easyRandom.nextObject(StringAndIntegerConstructorType.class);
//            StringAndIntegerConstructorType rnd = new StringAndIntegerConstructorType(easyRandom.nextObject(String.class), easyRandom.nextInt());
//            StringAndIntegerConstructorType rnd = new StringAndIntegerConstructorType(String.valueOf(easyRandom.nextInt()), easyRandom.nextInt());
//            StringAndIntegerConstructorType rnd = new StringAndIntegerConstructorType("1", 2);
            ints[cnt[0]++] = rnd;
        });
        long iterationTime = System.currentTimeMillis() - iterationBefore;
        long fullTime = System.currentTimeMillis() - fullBefore;
        System.err.printf("Finished in %s seconds, iteration in %s seconds%n", (fullTime / 1000.0), (iterationTime / 1000.0));
        System.out.println(new HashSet(Arrays.stream(ints).collect(Collectors.toList())).size());
        System.err.printf("Obj - %s%n", ints[0]);
        times.add(iterationTime);
        System.err.printf("Avg - %s%n", times.stream().mapToLong(Long::longValue).average().getAsDouble());
    }

    @Test
    @Disabled
    void pa2() throws Exception {
        int iterations = 2000000;
        StringAndIntegerConstructorType[] objects = new StringAndIntegerConstructorType[iterations];
        final int[] cnt = {0};
        long before = System.currentTimeMillis();
//        EasyRandom easyRandom = new EasyRandom();
        RNGesus easyRandom = new RNGesus();
        nonParallel(iterations, () -> {
            StringAndIntegerConstructorType rnd = easyRandom.nextObject(StringAndIntegerConstructorType.class);
            objects[cnt[0]++] = rnd;
        });
        System.err.printf("Finished in %s seconds%n", ((System.currentTimeMillis() - before) / 1000.0));
        System.out.println(new HashSet(Arrays.stream(objects).collect(Collectors.toList())).size());
        System.err.printf("Obj - %s", objects[0]);
    }
}
