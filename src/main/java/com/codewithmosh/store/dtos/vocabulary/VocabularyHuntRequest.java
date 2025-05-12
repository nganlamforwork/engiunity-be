package com.codewithmosh.store.dtos.vocabulary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyHuntRequest {
    @NotBlank(message = "Topic is required")
    @Size(min = 2, max = 100, message = "Topic must be between 2 and 100 characters")
    private String topic;

    @NotBlank(message = "CEFR level is required")
    @Pattern(regexp = "^(A1|A2|B1|B2|C1|C2)$", message = "Level must be one of: A1, A2, B1, B2, C1, C2")
    private String level;

    @NotNull
    @Size(min = 10, max = 50)
    private int wordCount;

    @NotNull
    private Long sessionId;
}

