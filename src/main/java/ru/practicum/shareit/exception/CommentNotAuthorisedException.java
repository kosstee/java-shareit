package ru.practicum.shareit.exception;

public class CommentNotAuthorisedException extends RuntimeException {
    public CommentNotAuthorisedException(String message) {
        super(message);
    }
}
