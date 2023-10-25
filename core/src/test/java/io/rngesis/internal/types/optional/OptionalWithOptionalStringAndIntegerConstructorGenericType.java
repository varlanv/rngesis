package io.rngesis.internal.types.optional;

import io.rngesis.internal.types.StringAndIntegerConstructorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class OptionalWithOptionalStringAndIntegerConstructorGenericType {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Optional<StringAndIntegerConstructorType>> optional;
}
