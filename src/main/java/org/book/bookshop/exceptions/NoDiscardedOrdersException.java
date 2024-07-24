package org.book.bookshop.exceptions;

public class NoDiscardedOrdersException extends RuntimeException{
    public NoDiscardedOrdersException(String msg) {
        super(msg);
    }
}
