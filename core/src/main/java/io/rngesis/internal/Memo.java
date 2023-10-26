package io.rngesis.internal;

import com.sun.jmx.remote.internal.ArrayQueue;
import io.rngesis.api.RNGesisModule;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

public class Memo {

    private final Class<?> requestedType;
    private final String requestedTypeName;
    private final int recursionDepth;
    @Getter
    private Class<?> currentType;
    private String currentTypeName;
    private Class<?> firstOccurrenceOfCurrentType;
    private String firstOccurrenceOfCurrentTypeName;
    private RNGesisModule<?> firstOccurrenceOfCurrentTypeModule;
    @Getter
    private Queue<RNGesisModule<?>> modules = new LinkedList<>();
    private int totalCount;
    private int countOfSameType;

    public Memo(Class<?> requestedType, String requestedTypeName) {
        this.requestedType = requestedType;
        this.requestedTypeName = requestedTypeName;
        this.currentType = requestedType;
        this.currentTypeName = requestedTypeName;
        this.firstOccurrenceOfCurrentType = requestedType;
        this.firstOccurrenceOfCurrentTypeName = requestedTypeName;
        this.recursionDepth = 5;
    }


    public void increment() {
        System.out.printf("iter - %s, current type - %s, current type name - %s%n", totalCount++, currentType, currentTypeName);
    }

    public boolean shouldStopRecursion() {
        return countOfSameType >= recursionDepth;
    }

    public void addModule(RNGesisModule<?> module) {
        modules.add(module);
    }

    public Memo withCurrentType(Class<?> type, String typeName) {
        if (type == currentType) {
            ++countOfSameType;
        } else {
            this.countOfSameType = 0;
            this.currentType = type;
            this.modules.clear();
        }
        return this;
    }
}
