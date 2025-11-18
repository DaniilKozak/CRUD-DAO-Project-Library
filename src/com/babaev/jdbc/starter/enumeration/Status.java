package com.babaev.jdbc.starter.enumeration;

public enum Status {
    AVAILABLE,
    TAKEN,
    LOST;

    // Внутри Enum Status
    public static Status fromString(String value) {
        if (value == null) {
            return null; // или кидать исключение
        }
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // ЛОГИРОВАТЬ ОШИБКУ!
            // И вернуть дефолтное значение, например:
            return LOST;
        }
    }
}
