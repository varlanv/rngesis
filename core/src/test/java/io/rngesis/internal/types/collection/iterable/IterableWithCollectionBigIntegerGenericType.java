package io.rngesis.internal.types.collection.iterable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class IterableWithCollectionBigIntegerGenericType {

    private final List<List<BigInteger>> list;
}
