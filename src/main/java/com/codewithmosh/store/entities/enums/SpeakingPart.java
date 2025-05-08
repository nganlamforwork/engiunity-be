package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
public enum SpeakingPart {
    /**
     * Full speaking test (all parts)
     */
    FULL("Full"),
    /**
     * Part 1 only - Introduction and interview
     */
    PART1("Part 1"),
    /**
     * Part 2 only - Individual long turn (cue card)
     */
    PART2("Part 2"),
    /**
     * Part 3 only - Two-way discussion
     */
    PART3("Part 3");
    private final String displayName;

    SpeakingPart(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static SpeakingPart fromString(String value) {
        for (SpeakingPart part : SpeakingPart.values()) {
            if (part.displayName.equalsIgnoreCase(value) || part.name().equalsIgnoreCase(value)) {
                return part;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
