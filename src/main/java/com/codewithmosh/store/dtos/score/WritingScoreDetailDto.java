/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.score;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Specific DTO for IELTS Writing score details
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class WritingScoreDetailDto extends ScoreDetailDto implements Serializable {
    @JsonProperty("task_response")
    private CategoryScoreDto taskResponse;

    @JsonProperty("coherence_and_cohesion")
    private CategoryScoreDto coherenceAndCohesion;

    @JsonProperty("lexical_resource")
    private CategoryScoreDto lexicalResource;

    @JsonProperty("grammatical_range_and_accuracy")
    private CategoryScoreDto grammaticalRangeAndAccuracy;

    @Override
    public double calculateTotalPossibleScore() {
        int totalScore = taskResponse.getScore() + coherenceAndCohesion.getScore()
                + lexicalResource.getScore() + grammaticalRangeAndAccuracy.getScore();

        double averageScore = totalScore / 4.0;
        return Math.round(averageScore * 2) / 2.0;
    }
}
