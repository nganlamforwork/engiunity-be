/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingSessionDto {
    private Long id;
    private Long userId;
    private SpeakingSessionStatus status;
    private String topic;
    private String notes;
    private SpeakingPart part;
}