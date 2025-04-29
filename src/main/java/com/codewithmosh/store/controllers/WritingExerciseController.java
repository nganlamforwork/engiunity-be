package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.scoring.writing.WritingEvaluationDTO;
import com.codewithmosh.store.dtos.writing.*;
import com.codewithmosh.store.services.WritingExerciseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("writing/exercises")
public class WritingExerciseController {

    private final WritingExerciseService writingExerciseService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WritingExerciseSummaryDto> create(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,
            UriComponentsBuilder uriBuilder) throws JsonProcessingException {
        // Parse JSON String -> CreateExerciseManuallyRequest
        CreateExerciseManuallyRequest request = objectMapper.readValue(data, CreateExerciseManuallyRequest.class);

//        System.out.println(request);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        WritingExerciseSummaryDto writingExerciseSummaryDto = writingExerciseService.createExercise(request, userId, image);

        var uri = uriBuilder.path("/writing/exercises/{id}")
                .buildAndExpand(writingExerciseSummaryDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(writingExerciseSummaryDto);
    }

    @GetMapping
    public ResponseEntity<Iterable<WritingExerciseSummaryDto>> getAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        Iterable<WritingExerciseSummaryDto> exercises = writingExerciseService.getAllExercisesByUser(userId);

        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WritingExerciseDto> getById(@PathVariable Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        WritingExerciseDto writingExerciseDto = writingExerciseService.getExerciseById(id, userId);
        return ResponseEntity.ok(writingExerciseDto);
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<String> createResponse(@PathVariable Long id,
                                            @RequestBody WritingExerciseResponseRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        writingExerciseService.createResponse(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Response submitted successfully.");
    }

    @PostMapping("/{id}/responses/submit")
    public ResponseEntity<String> submitResponse(@PathVariable Long id,
                                                 @RequestBody WritingExerciseResponseRequest request) {
//        System.out.println(request.getId());
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        writingExerciseService.submitResponse(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Response submitted successfully.");
    }


    @GetMapping("/{id}/responses/latest")
    public ResponseEntity<WritingExerciseResponseNotScoredDto> getLatestNotScoredResponse(@PathVariable Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        WritingExerciseResponseNotScoredDto latestNotScoredResponse = writingExerciseService.getLatestNotScoredResponse(id, userId);

        return ResponseEntity.ok(latestNotScoredResponse);
    }
}
