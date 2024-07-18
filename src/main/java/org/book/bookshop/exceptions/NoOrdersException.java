package org.book.bookshop.exceptions;

public class NoOrdersException extends RuntimeException {
    public NoOrdersException(String msg) {
        super(msg);
    }
}
