package io.rngesis.internal.types.collection.list;

import io.rngesis.internal.types.StringAndIntegerConstructorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ListWithStringAndIntegerConstructorGenericType {

    private final List<StringAndIntegerConstructorType> list;
}
