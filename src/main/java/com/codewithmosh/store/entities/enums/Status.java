package com.codewithmosh.store.entities.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 *
 * Enum representing the status of a task or exercise.
 */
public enum Status {

    DONE("Done"),
    IN_PROGRESS("In Progress"),
    NOT_STARTED("Not Started");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static Status fromString(String status) {
        for (Status s : Status.values()) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant with status " + status);
    }
}
