package com.codewithmosh.store.dtos.writing;

import com.codewithmosh.store.entities.enums.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WritingExerciseSummaryDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private CreationSource creationSource;
    private WritingPart part;
    private WritingExerciseType exerciseType;
    private Difficulty difficulty;
    private Status status;
    private Float score;
}
