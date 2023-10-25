package io.rngesis.api;

import java.util.List;

public interface RNGesis {

    <T> T nextObject(Class<T> type);

    <T> List<T> nextObjects(Class<T> type, int size);

    Integer nextInt();

    Long nextLong();

    String nextString();

    <T> RNGesis withModule(RNGesisModule<T> module);
}
