package io.rngesis.internal.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecursiveStringConstructorType {

    private final String value;
    private final RecursiveStringConstructorType next;
}
