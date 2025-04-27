package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WritingPart {

    PART_1("Part 1"),
    PART_2("Part 2");

    private final String title;

    WritingPart(String title) {
        this.title = title;
    }

    @JsonValue
    public String getTitle() {
        return title;
    }

    @JsonCreator
    public static WritingPart fromString(String title) {
        for (WritingPart part : WritingPart.values()) {
            if (part.getTitle().equalsIgnoreCase(title)) {
                return part;
            }
        }
        throw new IllegalArgumentException("No enum constant with title " + title);
    }
}
