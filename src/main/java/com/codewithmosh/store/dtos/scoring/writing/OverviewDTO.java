/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.scoring.writing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OverviewDTO {

    @JsonProperty("totalScore")
    private Integer totalScore;

    @JsonProperty("totalFeedback")
    private String totalFeedback;

    @JsonProperty("overallImprovementSuggestion")
    private String overallImprovementSuggestion;
}
