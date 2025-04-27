package com.codewithmosh.store.exceptions;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
public class DirectoryCreationException extends RuntimeException {
    public DirectoryCreationException(String message) {
        super(message);
    }
    public DirectoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
