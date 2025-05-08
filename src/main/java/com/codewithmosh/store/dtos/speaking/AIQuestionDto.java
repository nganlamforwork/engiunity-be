/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for an AI-generated question
 * Maps to the question objects in the AI response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIQuestionDto {

    @JsonProperty("part")
    private String part;

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("text")
    private String text;

    @JsonProperty("subQuestions")
    private List<String> subQuestions;

    @JsonProperty("cueCard")
    private List<String> cueCard;

    @JsonProperty("followUp")
    private List<String> followUp;

    /**
     * Convert the subQuestions list to a single string with line breaks
     * @return the subQuestions as a single string
     */
    public String getSubQuestionsAsString() {
        if (subQuestions == null || subQuestions.isEmpty()) {
            return null;
        }
        return String.join("\n", subQuestions);
    }

    /**
     * Convert the cueCard list to a single string with line breaks
     * @return the cueCard as a single string
     */
    public String getCueCardAsString() {
        if (cueCard == null || cueCard.isEmpty()) {
            return null;
        }
        return String.join("\n", cueCard);
    }

    /**
     * Convert the followUp list to a single string with line breaks
     * @return the followUp as a single string
     */
    public String getFollowUpAsString() {
        if (followUp == null || followUp.isEmpty()) {
            return null;
        }
        return String.join("\n", followUp);
    }
}