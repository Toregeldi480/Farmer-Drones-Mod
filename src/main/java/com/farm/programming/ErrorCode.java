package com.farm.programming;

public enum ErrorCode {
    ARG_NOT_FOUND(1),
    REDUNDANT_ARG(2),
    INVALID_TYPE(3),
    UNKNOWN(4);

    private final int value;
    private ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
