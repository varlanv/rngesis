package io.rngesis.internal.types.collection.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ListWithListWithListBigIntegerGenericType {

    private final List<List<List<BigInteger>>> list;
    private final List<String> strings;
}
