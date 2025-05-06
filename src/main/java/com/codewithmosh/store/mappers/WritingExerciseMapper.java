package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.writing.*;
import com.codewithmosh.store.entities.WritingExercise;
import com.codewithmosh.store.entities.WritingExerciseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WritingExerciseMapper {
    WritingExercise toEntity(CreateExerciseManuallyRequest request, Long userId);

    @Mapping(target = "thumbnail", expression = "java(fillThumbnail(writingExercise))")
    WritingExerciseDto toEntity(WritingExercise writingExercise);

    @Mapping(target = "thumbnail", expression = "java(fillThumbnail(writingExercise))")
    WritingExerciseSummaryDto toSummaryDto(WritingExercise writingExercise);

    WritingExerciseDto toDto(WritingExercise writingExercise);

    default String fillThumbnail(WritingExercise exercise) {
        if (exercise.getThumbnail() != null && !exercise.getThumbnail().isEmpty()) {
            return exercise.getThumbnail();
        } else if (exercise.getImage() != null && !exercise.getImage().isEmpty()) {
            return exercise.getImage();
        } else {
            return null;
        }
    }

    @Mapping(target = "takenAt", expression = "java(java.time.Instant.now())")
    WritingExerciseResponse toEntity(WritingExerciseResponseRequest request);

    WritingExerciseResponseNotScoredDto toNotScoredDto(WritingExerciseResponse response);

    WritingExerciseResponseDto toDto (WritingExerciseResponse response);
}
