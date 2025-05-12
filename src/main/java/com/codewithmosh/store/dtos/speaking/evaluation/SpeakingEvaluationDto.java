/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.dtos.speaking.evaluation;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingEvaluationDto implements ResponseDTO {

    @JsonProperty("overview")
    private OverviewDto overview;

    @JsonProperty("fluency_and_coherence")
    private CriteriaEvaluationDto fluencyAndCoherence;

    @JsonProperty("lexical_resource")
    private CriteriaEvaluationDto lexicalResource;

    @JsonProperty("grammatical_range_and_accuracy")
    private CriteriaEvaluationDto grammaticalRangeAndAccuracy;

    @JsonProperty("pronunciation")
    private CriteriaEvaluationDto pronunciation;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private TokenUsageDTO usage;
}