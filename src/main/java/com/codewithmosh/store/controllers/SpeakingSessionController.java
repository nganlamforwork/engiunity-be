/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.speaking.*;
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

    @GetMapping()
    public ResponseEntity<List<SpeakingSessionDto>> getSessions() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(speakingSessionService.getSessions(userId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpeakingSessionDto> getSession(@PathVariable Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(speakingSessionService.getSession(id, userId));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<SpeakingQuestionDto>> getQuestionsWithFilters(
            @PathVariable Long id,
            @RequestParam(required = false) String part,
            @RequestParam(required = false) Long questionId,
            @RequestParam(required = false) Integer order
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        SpeakingPart partEnum = null;
        if (part != null && !part.isBlank()) {
            try {
                partEnum = SpeakingPart.valueOf(part.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build(); // Hoặc bạn có thể trả thông báo lỗi rõ ràng
            }
        }

        List<SpeakingQuestionDto> result = speakingSessionService.getQuestionsByFilters(
                id, partEnum, questionId, order, userId
        );
        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}/responses")
    public ResponseEntity<Void> updateSessionResponses(@PathVariable Long id,
            @Valid @RequestBody UpdateSpeakingSessionResponsesRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        speakingSessionService.updateSessionResponses(request,id, userId);
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

        SpeakingEvaluationDto evaluation = speakingSessionService.scoreSession(id, userId);

        URI location = uriBuilder
                .path("/speaking/sessions/{id}/evaluation")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(evaluation);
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<SpeakingEvaluationDto> getSessionResult(@PathVariable Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(speakingSessionService.getEvaluationBySessionId(id, userId)) ;
    }
}
