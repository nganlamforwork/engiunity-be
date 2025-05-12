/**
 * Author: lamlevungan
 * Date: 12/05/2025
 **/
package com.codewithmosh.store.dtos.speaking;

import com.codewithmosh.store.entities.SpeakingSession;
import com.codewithmosh.store.entities.enums.ScoreStatus;
import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingEvaluationDto {
 private Long id;
 private Double score;
 private ScoreStatus scoreStatus;
 private Map<String, Object> scoreDetail;
 private SpeakingSessionDto speakingSession;
}