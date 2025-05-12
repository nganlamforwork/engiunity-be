/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpeakingResponseRequestDto {
    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("audioUrl")
    private String audioUrl;

    @JsonProperty("transcript")
    private String transcript;
}