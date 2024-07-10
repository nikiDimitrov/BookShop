package org.book.bookshop.exceptions;

public class NoCategoriesException extends RuntimeException {
    public NoCategoriesException(String msg) {
        super(msg);
    }
}
