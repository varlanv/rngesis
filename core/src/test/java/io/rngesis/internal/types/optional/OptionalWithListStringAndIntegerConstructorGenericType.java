package io.rngesis.internal.types.optional;

import io.rngesis.internal.types.StringAndIntegerConstructorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class OptionalWithListStringAndIntegerConstructorGenericType {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<List<StringAndIntegerConstructorType>> optional;
}
