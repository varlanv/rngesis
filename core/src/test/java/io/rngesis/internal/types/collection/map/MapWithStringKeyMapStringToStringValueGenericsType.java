package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithStringKeyMapStringToStringValueGenericsType {

    private final Map<String, Map<String, String>> map;
}
