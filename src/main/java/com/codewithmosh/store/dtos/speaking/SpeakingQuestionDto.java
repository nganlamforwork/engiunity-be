/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingQuestionDto {
    private Long id;
    private Integer part;
    private String text;
    private String subQuestions;
    private String cueCard;
    private String followUp;
    private Integer order;
}