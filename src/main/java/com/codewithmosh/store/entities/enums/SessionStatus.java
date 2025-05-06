package com.codewithmosh.store.entities.enums;

import lombok.Getter;

@Getter
public enum SessionStatus {
    // Step 1: Vocabulary Hunt - Words have been generated and displayed
    WORDS_GENERATED("Words Generated", "Vocabulary words have been generated"),

    // Step 2: Paragraph Generation - AI has generated paragraphs using the vocabulary
    PARAGRAPH_GENERATED("Paragraph Generated", "Practice paragraph has been generated"),

    // Step 3: Contextual Input - User is in reading/memorization phase
    READING("Reading", "User is reading and memorizing vocabulary"),

    // Step 4: Writing Output - User has started writing practice
    WRITING_STARTED("Writing Started", "User has started writing practice"),

    // Step 4 (continued): Feedback has been generated for the user's writing
    FEEDBACK_GENERATED("Feedback Generated", "Feedback has been generated for user's writing"),

    // Step 5: Save and Track - Session is complete and all steps have been finished
    COMPLETED("Completed", "Session has been completed");

    private final String displayName;
    private final String description;

    SessionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}