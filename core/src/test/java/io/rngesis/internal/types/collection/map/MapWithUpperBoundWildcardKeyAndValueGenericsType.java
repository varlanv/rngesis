package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithUpperBoundWildcardKeyAndValueGenericsType {

    private final Map<? extends Number, ? extends Number> map;
}
