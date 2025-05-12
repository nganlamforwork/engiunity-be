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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewDto {

    @JsonProperty("totalScore")
    private Double totalScore;

    @JsonProperty("overallFeedback")
    private String overallFeedback;

    @JsonProperty("overallImprovementSuggestion")
    private String overallImprovementSuggestion;
}