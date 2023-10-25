package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithUpperBoundWildcardValueGenericsType {

    private final Map<String, ? extends Number> map;
}
