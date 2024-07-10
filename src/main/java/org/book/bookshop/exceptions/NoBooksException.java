package org.book.bookshop.exceptions;

public class NoBooksException extends RuntimeException {
    public NoBooksException(String msg) {
        super(msg);
    }
}
