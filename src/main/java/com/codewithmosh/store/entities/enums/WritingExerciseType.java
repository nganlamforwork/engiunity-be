package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 *
 * Enum representing types of Writing Exercises.
 */
@Getter
public enum WritingExerciseType {

    // Line graph - shows trends over time
    LINE_GRAPH("Line Graph"),

    // Bar chart - compares quantities between categories
    BAR_CHART("Bar Chart"),

    // Pie chart - shows proportions in a whole
    PIE_CHART("Pie Chart"),

    // Table - displays data in a structured grid
    TABLE("Table"),

    // Process diagram - illustrates stages in a process
    PROCESS("Process Diagram"),

    // Map - illustrates geographical changes
    MAP("Map"),

    // Mixed chart - combines multiple chart types
    MIXED_CHART("Mixed Chart"),

    // Opinion essay - argue for or against a statement
    OPINION_ESSAY("Opinion Essay"),

    // Discussion essay - discuss both sides of an argument
    DISCUSSION_ESSAY("Discussion Essay"),

    // Problem and solution essay - state problems and suggest solutions
    PROBLEM_SOLUTION_ESSAY("Problem & Solution Essay"),

    // Advantage and disadvantage essay - discuss positives and negatives
    ADVANTAGE_DISADVANTAGE_ESSAY("Advantage & Disadvantage Essay"),

    // Two-part question essay - answer two questions from the prompt
    TWO_PART_QUESTION("Two-part Question Essay");

    private final String title;

    WritingExerciseType(String title) {
        this.title = title;
    }

    @JsonValue
    public String getTitle() {
        return title;
    }

    /**
     * Converts a string to the corresponding WritingExerciseType enum.
     * @param title the string representation of the exercise type.
     * @return the corresponding WritingExerciseType.
     * @throws IllegalArgumentException if no enum constant matches the given string.
     */
    @JsonCreator
    public static WritingExerciseType fromString(String title) {
        for (WritingExerciseType type : WritingExerciseType.values()) {
            if (type.getTitle().equalsIgnoreCase(title)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with title " + title);
    }
}
