package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
public enum SpeakingSessionStatus {
    /**
     * Session is being initialized
     */
    INIT("Init"),

    /**
     * Session is created with questions but not started by user
     */
    CREATED("Created"),

    IN_PROGRESS("In Progress"),

    /**
     * All parts have been submitted
     */
    SUBMITTED("Submitted"),

    /**
     * Session has been scored
     */
    SCORED("Scored");

    private final String displayName;

    SpeakingSessionStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static SpeakingSessionStatus fromString(String value) {
        for (SpeakingSessionStatus status : SpeakingSessionStatus.values()) {
            if (status.displayName.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }

}