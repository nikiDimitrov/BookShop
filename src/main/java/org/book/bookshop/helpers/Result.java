package org.book.bookshop.helpers;


public class Result<T> {
    private final T value;
    private final String error;


    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(String error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot get value from a failed Result.");
        }
        return value;
    }

    public String getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot get error from a successful Result.");
        }
        return error;
    }
}
