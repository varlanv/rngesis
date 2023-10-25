package io.rngesis.internal.types;

import lombok.Getter;

@Getter
public class StringAndIntegerSetterAndTwoConstructorType {

    private String stringConstructor;
    private Integer integerConstructor;
    private String stringSetter;
    private Integer integerSetter;

    public StringAndIntegerSetterAndTwoConstructorType(String stringConstructor, Integer integerConstructor) {
        this.stringConstructor = stringConstructor;
        this.integerConstructor = integerConstructor;
    }

    public StringAndIntegerSetterAndTwoConstructorType() {
    }

    public void setStringSetter(String stringSetter) {
        this.stringSetter = stringSetter;
    }

    public void setIntegerSetter(Integer integerSetter) {
        this.integerSetter = integerSetter;
    }
}
