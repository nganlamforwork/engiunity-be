/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpeakingResponseDto {

    private Long id;

    @JsonProperty("questionId")
    private Long questionId;

    @JsonProperty("audioUrl")
    private String audioUrl;

    @JsonProperty("transcript")
    private String transcript;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}