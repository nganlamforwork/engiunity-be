/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpeakingQuestionDto {
    private Long id;

    @JsonProperty("part")
    private SpeakingPart part;

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

    private SpeakingResponseDto response;

    /**
     * Convert the subQuestions list to a single string with line breaks
     *
     * @return the subQuestions as a single string
     */
    @JsonIgnore
    public String getSubQuestionsAsString() {
        if (subQuestions == null || subQuestions.isEmpty()) {
            return null;
        }
        return String.join("\n", subQuestions);
    }

    /**
     * Convert the cueCard list to a single string with line breaks
     *
     * @return the cueCard as a single string
     */
    @JsonIgnore
    public String getCueCardAsString() {
        if (cueCard == null || cueCard.isEmpty()) {
            return null;
        }
        return String.join("\n", cueCard);
    }

    /**
     * Convert the followUp list to a single string with line breaks
     *
     * @return the followUp as a single string
     */
    @JsonIgnore
    public String getFollowUpAsString() {
        if (followUp == null || followUp.isEmpty()) {
            return null;
        }
        return String.join("\n", followUp);
    }

    /**
     * Convert a string with line breaks into a list of sub-questions
     */
    public void setSubQuestionsFromString(String subQuestionsStr) {
        if (subQuestionsStr == null || subQuestionsStr.isBlank()) {
            this.subQuestions = null;
        } else {
            this.subQuestions = List.of(subQuestionsStr.split("\\R")); // \R = any line break
        }
    }

    /**
     * Convert a string with line breaks into a list of cue card items
     */
    public void setCueCardFromString(String cueCardStr) {
        if (cueCardStr == null || cueCardStr.isBlank()) {
            this.cueCard = null;
        } else {
            this.cueCard = List.of(cueCardStr.split("\\R"));
        }
    }

    /**
     * Convert a string with line breaks into a list of follow-up questions
     */
    public void setFollowUpFromString(String followUpStr) {
        if (followUpStr == null || followUpStr.isBlank()) {
            this.followUp = null;
        } else {
            this.followUp = List.of(followUpStr.split("\\R"));
        }
    }
}