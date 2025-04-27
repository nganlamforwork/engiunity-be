package com.codewithmosh.store.exceptions;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
