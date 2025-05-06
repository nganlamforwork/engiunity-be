package com.codewithmosh.store.dtos.vocabulary;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphRequest {
    @NotNull(message = "Session ID is required")
    private Long sessionId;
}