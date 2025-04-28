package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.writing.CreateExerciseManuallyRequest;
import com.codewithmosh.store.dtos.writing.WritingExerciseDto;
import com.codewithmosh.store.dtos.writing.WritingExerciseSummaryDto;
import com.codewithmosh.store.entities.WritingExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
@Mapper(componentModel = "spring")
public interface WritingExerciseMapper {
    WritingExercise toEntity(CreateExerciseManuallyRequest request, Long userId);

    WritingExerciseDto toDto(WritingExercise writingExercise);
    WritingExerciseSummaryDto toSummaryDto(WritingExercise writingExercise);
}
