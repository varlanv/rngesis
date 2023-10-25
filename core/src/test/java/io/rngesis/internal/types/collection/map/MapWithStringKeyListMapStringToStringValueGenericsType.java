package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithStringKeyListMapStringToStringValueGenericsType {

    private final Map<String, List<Map<String, String>>> map;
}
