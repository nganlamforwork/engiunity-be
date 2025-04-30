/**
 * Author: lamlevungan
 * Date: 30/04/2025
 **/
package com.codewithmosh.store.dtos.writing;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WritingExerciseResponseDto {
    private Integer id;
    private String content;
    private Instant takenAt;
    private Integer takeNumber;
    private Float score;
    private Map<String, Object> scoreDetail;
}
