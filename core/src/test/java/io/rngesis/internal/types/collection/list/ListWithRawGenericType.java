package io.rngesis.internal.types.collection.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ListWithRawGenericType {

    private final List list;
}
