package org.book.bookshop.exceptions;

public class NoUsersException extends RuntimeException {
    public NoUsersException(String message) {
        super(message);
    }
}
