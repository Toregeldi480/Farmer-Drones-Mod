package com.farm.programming;

public enum ResponseCode {
    SUCCESS(0),
    WAITING(1),
    ARG_NOT_FOUND_ERROR(11),
    REDUNDANT_ARG_ERROR(12),
    INVALID_TYPE_ERROR(13),
    UNKNOWN_ERROR(14);

    private final int value;
    private ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
