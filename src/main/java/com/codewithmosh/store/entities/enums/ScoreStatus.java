package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ScoreStatus {
    NOT_SCORED("Chưa chấm"),
    IN_PROGRESS("Đang chấm"),
    SCORED("Đã chấm");

    private final String description;

    ScoreStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ScoreStatus fromString(String description) {
        for (ScoreStatus status : ScoreStatus.values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }
}
