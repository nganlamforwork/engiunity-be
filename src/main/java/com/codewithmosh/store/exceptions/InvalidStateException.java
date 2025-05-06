package com.codewithmosh.store.exceptions;

public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }
}