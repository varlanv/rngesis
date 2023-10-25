package io.rngesis.internal;

import io.rngesis.internal.types.*;
import io.rngesis.internal.types.collection.iterable.IterableWithCollectionBigIntegerGenericType;
import io.rngesis.internal.types.collection.list.*;
import io.rngesis.internal.types.collection.map.*;
import io.rngesis.internal.types.collection.set.SetWithListBigIntegerGenericType;
import io.rngesis.internal.types.optional.OptionalWithListStringAndIntegerConstructorGenericType;
import io.rngesis.internal.types.optional.OptionalWithOptionalStringAndIntegerConstructorGenericType;
import io.rngesis.internal.types.optional.OptionalWithStringAndIntegerConstructorGenericType;
import io.rngesis.test.BaseStatelessUnitTest;
import lombok.val;
import lombok.var;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

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
    @Disabled("todo")
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
    @Disabled("todo")
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
        Assertions.assertThat(actual.getList()).doesNotContainNull();
        Assertions.assertThat(actual.getList()).doesNotHaveDuplicates();
    }

    @Test
    void nextObject_unknown_type_when_list_with_generic_list_of_bigints_then_should_create_object() {
        val actual = subject.nextObject(ListWithListBigIntegerGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getList()).isNotNull();
        Assertions.assertThat(actual.getList()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getList()).doesNotContainNull();
        Assertions.assertThat(actual.getList()).doesNotHaveDuplicates();
        for (val nestedActual : actual.getList()) {
            Assertions.assertThat(nestedActual).isNotNull();
            Assertions.assertThat(nestedActual).hasSizeBetween(1, 3);
            for (val nestedNestedActual : nestedActual) {
                Assertions.assertThat(nestedNestedActual).isNotNull();
            }
        }
    }

    @Test
    void nextObject_unknown_type_when_list_with_generic_list_of_bigints_then_all_bigints_should_be_unique() {
        val actual = subject.nextObject(ListWithListBigIntegerGenericType.class);
        val bigIntegers = new HashSet<>();
        var count = 0;

        for (val nestedActual : actual.getList()) {
            for (val nestedNestedActual : nestedActual) {
                bigIntegers.add(nestedNestedActual);
                count++;
            }
        }

        Assertions.assertThat(bigIntegers.size()).isEqualTo(count);
    }

    @Test
    void nextObject_unknown_type_when_iterable_with_generic_collection_of_bigints_then_all_bigints_should_be_unique() {
        val actual = subject.nextObject(IterableWithCollectionBigIntegerGenericType.class);
        val bigIntegers = new HashSet<>();
        var count = 0;

        for (val nestedActual : actual.getList()) {
            for (val nestedNestedActual : nestedActual) {
                bigIntegers.add(nestedNestedActual);
                count++;
            }
        }

        Assertions.assertThat(bigIntegers.size()).isEqualTo(count);
    }

    @Test
    void nextObject_unknown_type_when_set_with_generic_list_of_bigints_then_all_bigints_should_be_unique() {
        val actual = subject.nextObject(SetWithListBigIntegerGenericType.class);
        val bigIntegers = new HashSet<>();
        var count = 0;

        for (val nestedActual : actual.getList()) {
            for (val nestedNestedActual : nestedActual) {
                bigIntegers.add(nestedNestedActual);
                count++;
            }
        }

        Assertions.assertThat(bigIntegers.size()).isEqualTo(count);
    }

    @Test
    void nextObject_unknown_type_when_optional_with_generic_of_string_and_integer_constructor_type_then_should_create_object() {
        val actual = subject.nextObject(OptionalWithStringAndIntegerConstructorGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getOptional()).isNotNull();
        Assertions.assertThat(actual.getOptional()).isPresent();
        Assertions.assertThat(actual.getOptional().get().getString()).isNotBlank();
        Assertions.assertThat(actual.getOptional().get().getInteger()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_optional_with_generic_optional_of_string_and_integer_constructor_type_then_should_create_object() {
        val actual = subject.nextObject(OptionalWithOptionalStringAndIntegerConstructorGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getOptional()).isNotNull();
        Assertions.assertThat(actual.getOptional()).isPresent();
        Assertions.assertThat(actual.getOptional().get()).isNotNull();
        Assertions.assertThat(actual.getOptional().get()).isPresent();
        Assertions.assertThat(actual.getOptional().get().get().getString()).isNotBlank();
        Assertions.assertThat(actual.getOptional().get().get().getInteger()).isNotNull().isNotZero();
    }

    @Test
    void nextObject_unknown_type_when_optional_with_generic_list_of_string_and_integer_constructor_type_then_should_create_object() {
        val actual = subject.nextObject(OptionalWithListStringAndIntegerConstructorGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getOptional()).isNotNull();
        Assertions.assertThat(actual.getOptional()).isPresent();
        Assertions.assertThat(actual.getOptional().get()).isNotNull();
        Assertions.assertThat(actual.getOptional().get()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getOptional().get()).doesNotHaveDuplicates();
        Assertions.assertThat(actual.getOptional().get()).doesNotContainNull();
    }

    @Test
    void nextObject_unknown_type_when_list_with_no_generic_then_should_create_empty_list() {
        val actual = subject.nextObject(ListWithNoGenericType.class);

        Assertions.assertThat(actual.getList()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_list_with_raw_generic_then_should_create_empty_list() {
        val actual = subject.nextObject(ListWithRawGenericType.class);

        Assertions.assertThat(actual.getList()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_raw_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithRawGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_unbound_wildcard_key_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUnboundWildcardKeyGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_unbound_wildcard_value_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUnboundWildcardValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_unbound_wildcard_key_and_value_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUnboundWildcardKeyAndValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_upper_bound_wildcard_value_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUpperBoundWildcardValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_upper_bound_wildcard_key_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUpperBoundWildcardKeyGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_upper_bound_wildcard_key_and_value_generic_then_should_create_empty_map() {
        val actual = subject.nextObject(MapWithUpperBoundWildcardKeyAndValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isEmpty();
    }

    @Test
    void nextObject_unknown_type_when_map_with_string_key_string_value_then_should_create_non_empty_map() {
        val actual = subject.nextObject(MapWithStringKeyStringValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isNotEmpty();
        Assertions.assertThat(actual.getMap()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getMap().keySet()).doesNotContainNull();
        Assertions.assertThat(actual.getMap().values()).doesNotContainNull();
        Assertions.assertThat(actual.getMap().keySet()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getMap().values()).doesNotHaveDuplicates();
    }

    @Test
    void nextObject_unknown_type_when_map_with_string_key_map_string_key_string_value_then_should_create_non_empty_map() {
        val actual = subject.nextObject(MapWithStringKeyMapStringToStringValueGenericsType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getMap()).isNotNull().isNotEmpty();
        Assertions.assertThat(actual.getMap()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getMap().keySet()).doesNotContainNull();
        Assertions.assertThat(actual.getMap().values()).doesNotContainNull();
        Assertions.assertThat(actual.getMap().keySet()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getMap().values()).doesNotHaveDuplicates();
    }

    @Test
    void nextObject_unknown_type_when_outcome_type_then_should_create_object() {
        val actual = subject.nextObject(OutcomeType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getId()).isNotNull();
        Assertions.assertThat(actual.getName()).isNotBlank();
        Assertions.assertThat(actual.getUpdateTime()).isNotNull();
        Assertions.assertThat(actual.getTranslations()).isNotNull().isNotEmpty();
        Assertions.assertThat(actual.getTranslations()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getTranslations().values()).doesNotContainNull();
        Assertions.assertThat(actual.getTranslations().values()).doesNotHaveDuplicates();
        Assertions.assertThat(actual.getTranslations().keySet()).doesNotContainNull();
    }

    @RepeatedTest(1)
    @Disabled
    void kek() {
        val actual = subject.nextObject(OutcomeType.class);
//        val subject = new EasyRandom(new EasyRandomParameters().collectionSizeRange(1, 3));
        val count = 2_000_000;
        val objs = new Object[count];
        val before = System.currentTimeMillis();
        for (var i = 0; i < count; i++) {
            objs[i] = subject.nextObject(OutcomeType.class);
        }
        val time = System.currentTimeMillis() - before;
        System.err.println(objs.length + " Time taken - " + time);
    }

    @Test
    void nextObject_unknown_type_when_map_with_string_key_map_string_key_string_value_then_should_create_nested_maps() {
        val actual = subject.nextObject(MapWithStringKeyMapStringToStringValueGenericsType.class);

        actual.getMap().forEach((key, valueMap) -> {
            Assertions.assertThat(key).isNotBlank();
            Assertions.assertThat(valueMap).isNotNull().isNotEmpty();
            Assertions.assertThat(valueMap).hasSizeBetween(1, 3);
            Assertions.assertThat(valueMap.keySet()).doesNotContainNull();
            Assertions.assertThat(valueMap.values()).doesNotContainNull();
            Assertions.assertThat(valueMap.keySet()).hasSizeBetween(1, 3);
            Assertions.assertThat(valueMap.values()).doesNotHaveDuplicates();
        });
    }

    @Test
    void nextObject_unknown_type_when_map_with_string_key_list_map_string_key_string_value_then_should_create_nested_maps() {
        val actual = subject.nextObject(MapWithStringKeyListMapStringToStringValueGenericsType.class);

        actual.getMap().forEach((key, valueList) -> {
            Assertions.assertThat(key).isNotBlank();
            Assertions.assertThat(valueList).isNotNull().isNotEmpty();
            Assertions.assertThat(valueList).hasSizeBetween(1, 3);
            valueList.forEach(valueMap -> {
                Assertions.assertThat(valueMap.keySet()).doesNotContainNull();
                Assertions.assertThat(valueMap.values()).doesNotContainNull();
                Assertions.assertThat(valueMap.keySet()).hasSizeBetween(1, 3);
                Assertions.assertThat(valueMap.values()).doesNotHaveDuplicates();
            });
        });
    }

    @Test
    void nextObject_unknown_type_when_list_with_generic_list_of_list_of_bigints_then_should_create_object() {
        val actual = subject.nextObject(ListWithListWithListBigIntegerGenericType.class);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getList()).isNotNull();
        Assertions.assertThat(actual.getList()).hasSizeBetween(1, 3);
        Assertions.assertThat(actual.getList()).doesNotContainNull();
        Assertions.assertThat(actual.getList()).doesNotHaveDuplicates();
        for (val nestedActual : actual.getList()) {
            Assertions.assertThat(nestedActual).isNotNull();
            Assertions.assertThat(nestedActual).hasSizeBetween(1, 3);
            for (val nestedNestedActual : nestedActual) {
                Assertions.assertThat(nestedNestedActual).isNotNull();
                Assertions.assertThat(nestedNestedActual).hasSizeBetween(1, 3);
                for (val nestedNestedNestedActual : nestedNestedActual) {
                    Assertions.assertThat(nestedNestedNestedActual).isNotNull();
                }
            }
        }
    }

    @Test
    void nextObject_unknown_type_when_list_with_generic_list_of_list_of_bigints_then_all_bigints_should_be_unique() {
        val actual = subject.nextObject(ListWithListWithListBigIntegerGenericType.class);
        val bigIntegers = new HashSet<>();
        var count = 0;

        for (val nestedActual : actual.getList()) {
            for (val nestedNestedActual : nestedActual) {
                for (val nestedNestedNestedActual : nestedNestedActual) {
                    bigIntegers.add(nestedNestedNestedActual);
                    count++;
                }
            }
        }

        Assertions.assertThat(bigIntegers.size()).isEqualTo(count);
    }

    @Test
    void nextObject_unknown_type_when_list_with_generic_complex_object_then_nested_objects_should_be_filled_with_random_data() {
        val actual = subject.nextObject(ListWithStringAndIntegerConstructorGenericType.class);

        for (val nestedActual : actual.getList()) {
            Assertions.assertThat(nestedActual.getInteger()).isNotNull();
            Assertions.assertThat(nestedActual.getString()).isNotNull();
        }
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
    @Disabled
    void pa() throws Exception {
        val iterations = 2_000_000;
        val ints = new StringAndIntegerConstructorType[iterations];
        final int[] cnt = {0};
        val fullBefore = System.currentTimeMillis();
//        val easyRandom = new EasyRandom();
        val easyRandom = new RNGesus();
//        val easyRandom = new Random();
        val constructor = StringAndIntegerConstructorType.class.getConstructor(String.class, Integer.class);
        easyRandom.nextObject(StringAndIntegerConstructorType.class);
        val iterationBefore = System.currentTimeMillis();
        nonParallel(iterations, () -> {
//            val rnd = new Rnd(String.valueOf(easyRandom.nextObject(String.class)), easyRandom.nextInt());
//            val rnd = constructor.newInstance(String.valueOf(easyRandom.nextObject(String.class)), easyRandom.nextInt());
//            val rnd = constructor.newInstance("1", 2);
//            val rnd = constructor.newInstance(easyRandom.nextObject(String.class), easyRandom.nextInt());
//            val rnd = constructor.newInstance(String.valueOf(easyRandom.nextInt()), easyRandom.nextInt());
            val rnd = easyRandom.nextObject(StringAndIntegerConstructorType.class);
//            val rnd = new StringAndIntegerConstructorType(easyRandom.nextObject(String.class), easyRandom.nextInt());
//            val rnd = new StringAndIntegerConstructorType(String.valueOf(easyRandom.nextInt()), easyRandom.nextInt());
//            val rnd = new StringAndIntegerConstructorType("1", 2);
            ints[cnt[0]++] = rnd;
        });
        val iterationTime = System.currentTimeMillis() - iterationBefore;
        val fullTime = System.currentTimeMillis() - fullBefore;
        System.err.printf("Finished in %s seconds, iteration in %s seconds%n", (fullTime / 1000.0), (iterationTime / 1000.0));
        System.out.println(new HashSet<>(Arrays.stream(ints).collect(Collectors.toList())).size());
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
