/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.writing.scoring;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WritingEvaluationDto implements ResponseDTO {

    @JsonProperty("overview")
    private OverviewDto overview;

    @JsonProperty("task_response")
    private CategoryDto taskResponse;

    @JsonProperty("coherence_and_cohesion")
    private CategoryDto coherenceAndCohesion;

    @JsonProperty("lexical_resource")
    private CategoryDto lexicalResource;

    @JsonProperty("grammatical_range_and_accuracy")
    private CategoryDto grammaticalRangeAndAccuracy;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private TokenUsageDTO usage;
}
