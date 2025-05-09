/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.writing.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OverviewDto {

    @JsonProperty("totalScore")
    private Float totalScore;

    @JsonProperty("totalFeedback")
    private String totalFeedback;

    @JsonProperty("overallImprovementSuggestion")
    private String overallImprovementSuggestion;
}
