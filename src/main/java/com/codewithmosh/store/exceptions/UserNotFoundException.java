package com.codewithmosh.store.exceptions;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User with ID " + userId + " not found");
    }
}
