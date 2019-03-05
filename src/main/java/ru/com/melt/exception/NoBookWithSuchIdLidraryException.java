package ru.com.melt.exception;

public class NoBookWithSuchIdLidraryException extends RuntimeException {

    public NoBookWithSuchIdLidraryException() {
        super();
    }

    public NoBookWithSuchIdLidraryException(String bookId) {
        super(bookId);
    }
}
