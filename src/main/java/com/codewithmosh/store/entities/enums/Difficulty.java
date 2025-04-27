package com.codewithmosh.store.entities.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 *
 * Enum representing difficulty levels for writing exercises.
 */
public enum Difficulty {

    // Basic levels of difficulty
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),

    // Language proficiency levels (Common European Framework of Reference for Languages)
    A1("A1"),
    A2("A2"),
    B1("B1"),
    B2("B2"),
    C1("C1"),
    C2("C2");

    private final String level;

    Difficulty(String level) {
        this.level = level;
    }

    @JsonValue
    public String getLevel() {
        return level;
    }

    @JsonCreator
    public static Difficulty fromString(String level) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.getLevel().equalsIgnoreCase(level)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("No enum constant with level " + level);
    }
}
