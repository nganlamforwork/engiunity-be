/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.speaking.CreateSpeakingSessionRequest;
import com.codewithmosh.store.dtos.speaking.SpeakingSessionDto;
import com.codewithmosh.store.services.SpeakingSessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Valid @RequestBody CreateSpeakingSessionRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        SpeakingSessionDto response = speakingSessionService.createSession(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
