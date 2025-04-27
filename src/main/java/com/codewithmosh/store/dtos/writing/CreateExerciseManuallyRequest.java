/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
package com.codewithmosh.store.dtos.writing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateExerciseManuallyRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String thumbnail;

    @NotBlank(message = "Creation source is required")
    private String creationSource;

    @NotBlank(message = "Part is required")
    private String part;

    private String exerciseType;

    private String difficulty;

    @NotBlank(message = "Content is required")
    private String content;
}
