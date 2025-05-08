/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the AI-generated speaking session response
 * Maps directly to the JSON structure returned by the AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AISpeakingSessionResponseDto {
    @JsonProperty("questions")
    private List<AIQuestionDto> questions;
}