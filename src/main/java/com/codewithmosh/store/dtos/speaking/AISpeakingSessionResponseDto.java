/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * DTO for the AI-generated speaking session response
 * Maps directly to the JSON structure returned by the AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AISpeakingSessionResponseDto  implements ResponseDTO {
    @JsonProperty("questions")
    private List<SpeakingQuestionDto> questions;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private TokenUsageDTO usage;
}