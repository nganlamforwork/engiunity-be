/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.speaking.*;
import com.codewithmosh.store.dtos.speaking.evaluation.SpeakingEvaluationDto;
import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.services.SpeakingSessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("speaking/sessions")
public class SpeakingSessionController {
    private final SpeakingSessionService speakingSessionService;
    /**
     * Create a new speaking session
     * @param request the creation request
     * @return the created session
     */
    @PostMapping
    public ResponseEntity<SpeakingSessionDto> createSession(
            @Valid @RequestBody CreateSpeakingSessionRequest request,
        UriComponentsBuilder uriBuilder) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        SpeakingSessionDto response = speakingSessionService.createSession(request, userId);
        URI location = uriBuilder
                .path("/speaking/sessions/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeakingSessionDto> getSession(@PathVariable Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(speakingSessionService.getSession(id));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<SpeakingQuestionDto>> getQuestionsWithFilters(
            @PathVariable Long id,
            @RequestParam(required = false) String part,
            @RequestParam(required = false) Long questionId,
            @RequestParam(required = false) Integer order
    ) {
        List<SpeakingQuestionDto> result = speakingSessionService.getQuestionsByFilters(
                id, SpeakingPart.fromString(part), questionId, order
        );
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/responses")
    public ResponseEntity<Void> updateSessionResponses(@PathVariable Long id,
            @Valid @RequestBody UpdateSpeakingSessionResponsesRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        speakingSessionService.updateSessionResponses(request,id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Score a speaking session
     * @param request the scoring request
     * @return the evaluation result
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<SpeakingEvaluationDto> scoreSession(@PathVariable Long id,
            UriComponentsBuilder uriBuilder) {

        // Verify user authentication
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        SpeakingEvaluationDto evaluation = speakingSessionService.scoreSession(id);

        URI location = uriBuilder
                .path("/speaking/sessions/{id}/evaluation")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(evaluation);
    }
}
