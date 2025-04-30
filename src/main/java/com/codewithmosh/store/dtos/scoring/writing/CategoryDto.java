/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.scoring.writing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("feedback")
    private String feedback;

    @JsonProperty("corrections")
    private List<CorrectionDto> corrections;

    @JsonProperty("improvementSuggestion")
    private String improvementSuggestion;

    // Getters and setters
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<CorrectionDto> getCorrections() {
        return corrections;
    }

    public void setCorrections(List<CorrectionDto> corrections) {
        this.corrections = corrections;
    }

    public String getImprovementSuggestion() {
        return improvementSuggestion;
    }

    public void setImprovementSuggestion(String improvementSuggestion) {
        this.improvementSuggestion = improvementSuggestion;
    }
}
