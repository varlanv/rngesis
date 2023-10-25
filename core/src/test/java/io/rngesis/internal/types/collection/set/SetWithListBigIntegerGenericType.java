package io.rngesis.internal.types.collection.set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class SetWithListBigIntegerGenericType {

    private final Set<List<BigInteger>> list;
}
