/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSpeakingSessionRequest {
    @NotNull(message = "Session ID is required")
    private Long sessionId;

    // This is a stub - will be expanded later with actual scoring parameters
    // For example:
    // private Float fluencyScore;
    // private Float vocabularyScore;
    // private Float grammarScore;
    // private Float pronunciationScore;
    // private String feedback;
}
