/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.writing;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WritingExerciseResponseRequest {
    private Long id;

    @NotBlank(message = "Content is required")
    private String content;
}