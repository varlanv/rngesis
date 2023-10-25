package io.rngesis.internal.types.collection.list;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ListWithNoGenericType {

    private final List<?> list;
}
