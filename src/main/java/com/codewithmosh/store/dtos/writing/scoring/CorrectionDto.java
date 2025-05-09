/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.writing.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CorrectionDto {

    @JsonProperty("error")
    private String error;

    @JsonProperty("start_position")
    private Integer startPosition;

    @JsonProperty("end_position")
    private Integer endPosition;

    @JsonProperty("suggestion")
    private String suggestion;

    // Getters and setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}

