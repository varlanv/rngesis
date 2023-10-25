package io.rngesis.internal.types.collection.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MapWithStringKeyStringValueGenericsType {

    private final Map<String, String> map;
}
