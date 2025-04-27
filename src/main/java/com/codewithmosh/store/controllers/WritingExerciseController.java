package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.writing.CreateExerciseManuallyRequest;
import com.codewithmosh.store.dtos.writing.WritingExerciseDto;
import com.codewithmosh.store.services.WritingExerciseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("writing/exercises")
public class WritingExerciseController {

    private final WritingExerciseService writingExerciseService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WritingExerciseDto> create(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,
            UriComponentsBuilder uriBuilder) throws JsonProcessingException {
        // Parse JSON String -> CreateExerciseManuallyRequest
        CreateExerciseManuallyRequest request = objectMapper.readValue(data, CreateExerciseManuallyRequest.class);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        WritingExerciseDto writingExerciseDto = writingExerciseService.createExercise(request, userId, image);

        var uri = uriBuilder.path("/writing/exercises/{id}")
                .buildAndExpand(writingExerciseDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(writingExerciseDto);
    }
}
