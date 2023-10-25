package io.rngesis.internal.types.collection.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ListWithBigIntegerGenericType {

    private final List<BigInteger> list;
}
