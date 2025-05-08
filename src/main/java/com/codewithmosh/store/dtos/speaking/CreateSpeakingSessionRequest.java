/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpeakingSessionRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    private String notes;

    @NotNull(message = "Speaking part is required")
    private SpeakingPart part;
}