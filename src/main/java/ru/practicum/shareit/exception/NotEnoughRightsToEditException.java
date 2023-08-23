package ru.practicum.shareit.exception;

public class NotEnoughRightsToEditException extends RuntimeException {
    public NotEnoughRightsToEditException(String message) {
        super(message);
    }
}