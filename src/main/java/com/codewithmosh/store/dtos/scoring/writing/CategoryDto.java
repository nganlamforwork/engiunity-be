package com.codewithmosh.store.dtos.scoring.writing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryDto {
    @JsonProperty("score")
    private Integer score;

    @JsonProperty("feedback")
    private String feedback;

    @JsonProperty("corrections")
    private List<CorrectionDto> corrections;

    @JsonProperty("improvementSuggestion")
    private String improvementSuggestion;
}
