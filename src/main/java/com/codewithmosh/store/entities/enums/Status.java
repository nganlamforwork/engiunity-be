package com.codewithmosh.store.entities.enums;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 *
 * Enum representing the status of a task or exercise.
 */
public enum Status {

    // The status of the exercise
    DONE("Done"),
    IN_PROGRESS("In Progress"),
    NOT_STARTED("Not Started");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Converts a string to the corresponding Status enum.
     * @param status the string representation of the status.
     * @return the corresponding Status enum.
     * @throws IllegalArgumentException if no enum constant matches the given string.
     */
    public static Status fromString(String status) {
        for (Status s : Status.values()) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant with status " + status);
    }
}
