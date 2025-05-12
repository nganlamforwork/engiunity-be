/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.dtos.speaking.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaEvaluationDto {

    @JsonProperty("score")
    private Double score;

    @JsonProperty("feedback")
    private String feedback;

    @JsonProperty("examples")
    private List<ExampleDto> examples;

    @JsonProperty("improvementSuggestion")
    private String improvementSuggestion;
}