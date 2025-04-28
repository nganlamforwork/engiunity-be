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

    @Mapping(target = "thumbnail", expression = "java(fillThumbnail(writingExercise))")
    WritingExerciseDto toDto(WritingExercise writingExercise);

    @Mapping(target = "thumbnail", expression = "java(fillThumbnail(writingExercise))")
    WritingExerciseSummaryDto toSummaryDto(WritingExercise writingExercise);

    default String fillThumbnail(WritingExercise exercise) {
        if (exercise.getThumbnail() != null && !exercise.getThumbnail().isEmpty()) {
            return exercise.getThumbnail();
        } else if (exercise.getImage() != null && !exercise.getImage().isEmpty()) {
            return exercise.getImage();
        } else {
            return null;
        }
    }
}
