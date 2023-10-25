package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithUpperBoundWildcardKeyGenericsType {

    private final Map<? extends Number, String> map;
}
