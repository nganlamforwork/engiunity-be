/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.dtos.scoring.writing.WritingEvaluationDto;
import com.codewithmosh.store.services.WritingEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/writing")
public class WritingEvaluationController {

    private final WritingEvaluationService writingEvaluationService;

    @Autowired
    public WritingEvaluationController(WritingEvaluationService writingEvaluationService) {
        this.writingEvaluationService = writingEvaluationService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<WritingEvaluationDto> evaluateWriting(@RequestBody Map<String, String> request) {
        String writingSample = request.get("writingSample");
        String taskDescription = request.get("taskDescription");

        WritingEvaluationDto evaluation = writingEvaluationService.evaluateWriting(writingSample, taskDescription);

        return ResponseEntity.ok(evaluation);
    }

    @PostMapping("/evaluate/custom")
    public ResponseEntity<Object> evaluateWritingWithCustomSchema(@RequestBody Map<String, Object> request) {
        String writingSample = (String) request.get("writingSample");
        String taskDescription = (String) request.get("taskDescription");
        String customSchema = (String) request.get("schema");

        // Use Object.class as we don't know the exact type at compile time
        Object evaluation = writingEvaluationService.evaluateWritingWithCustomSchema(
                writingSample, taskDescription, customSchema, Object.class);

        return ResponseEntity.ok(evaluation);
    }
}
