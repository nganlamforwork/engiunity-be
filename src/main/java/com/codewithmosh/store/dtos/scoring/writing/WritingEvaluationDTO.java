/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.scoring.writing;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WritingEvaluationDTO implements ResponseDTO {

    @JsonProperty("overview")
    private OverviewDTO overview;

    @JsonProperty("task_response")
    private CategoryDTO taskResponse;

    @JsonProperty("coherence_and_cohesion")
    private CategoryDTO coherenceAndCohesion;

    @JsonProperty("lexical_resource")
    private CategoryDTO lexicalResource;

    @JsonProperty("grammatical_range_and_accuracy")
    private CategoryDTO grammaticalRangeAndAccuracy;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private TokenUsageDTO usage;

    // Getters and setters
    public OverviewDTO getOverview() {
        return overview;
    }

    public void setOverview(OverviewDTO overview) {
        this.overview = overview;
    }

    public CategoryDTO getTaskResponse() {
        return taskResponse;
    }

    public void setTaskResponse(CategoryDTO taskResponse) {
        this.taskResponse = taskResponse;
    }

    public CategoryDTO getCoherenceAndCohesion() {
        return coherenceAndCohesion;
    }

    public void setCoherenceAndCohesion(CategoryDTO coherenceAndCohesion) {
        this.coherenceAndCohesion = coherenceAndCohesion;
    }

    public CategoryDTO getLexicalResource() {
        return lexicalResource;
    }

    public void setLexicalResource(CategoryDTO lexicalResource) {
        this.lexicalResource = lexicalResource;
    }

    public CategoryDTO getGrammaticalRangeAndAccuracy() {
        return grammaticalRangeAndAccuracy;
    }

    public void setGrammaticalRangeAndAccuracy(CategoryDTO grammaticalRangeAndAccuracy) {
        this.grammaticalRangeAndAccuracy = grammaticalRangeAndAccuracy;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
