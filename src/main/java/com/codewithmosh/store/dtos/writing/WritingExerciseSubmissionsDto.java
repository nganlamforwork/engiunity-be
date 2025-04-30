/**
 * Author: lamlevungan
 * Date: 30/04/2025
 **/
package com.codewithmosh.store.dtos.writing;

import com.codewithmosh.store.entities.WritingExercise;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WritingExerciseSubmissionsDto {
    WritingExerciseDto exercise;
    List<WritingExerciseResponseDto> submissions;
}
