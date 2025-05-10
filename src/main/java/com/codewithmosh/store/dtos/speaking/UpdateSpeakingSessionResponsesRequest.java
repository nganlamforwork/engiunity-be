/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateSpeakingSessionResponsesRequest {
    @JsonProperty("responses")
    private List<SpeakingResponseRequestDto> responses;
}