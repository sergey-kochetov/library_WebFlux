package ru.com.melt.exception;

public class NoCommentWithSuchIdLidraryException extends RuntimeException {

    public NoCommentWithSuchIdLidraryException() {
        super();
    }

    public NoCommentWithSuchIdLidraryException(String commentId) {
        super(commentId);
    }
}
