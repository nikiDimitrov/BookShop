package org.book.bookshop.exceptions;

public class NoActiveOrdersException extends RuntimeException {
    public NoActiveOrdersException(String msg) {
        super(msg);
    }
}
