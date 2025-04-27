package com.codewithmosh.store.dtos.writing;

import com.codewithmosh.store.entities.enums.CreationSource;
import com.codewithmosh.store.entities.enums.Difficulty;
import com.codewithmosh.store.entities.enums.Status;
import com.codewithmosh.store.entities.enums.WritingExerciseType;
import com.codewithmosh.store.entities.enums.WritingPart;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WritingExerciseDto {
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
    private String content;
    private String image;
}
