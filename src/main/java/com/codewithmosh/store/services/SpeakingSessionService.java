/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.speaking.CreateSpeakingSessionRequest;
import com.codewithmosh.store.dtos.speaking.SpeakingSessionDto;
import com.codewithmosh.store.entities.SpeakingSession;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import com.codewithmosh.store.mapppers.SpeakingQuestionMapper;
import com.codewithmosh.store.mapppers.SpeakingSessionMapper;
import com.codewithmosh.store.repositories.SpeakingQuestionRepository;
import com.codewithmosh.store.repositories.SpeakingSessionRepository;
import com.codewithmosh.store.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SpeakingSessionService {
    private final SpeakingSessionMapper speakingSessionMapper;
    private final SpeakingQuestionMapper speakingQuestionMapper;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final SpeakingSessionRepository speakingSessionRepository;
    private final SpeakingQuestionRepository speakingQuestionRepository;

    public SpeakingSessionDto createSession(CreateSpeakingSessionRequest request, Long userId) {
        SpeakingSession session = speakingSessionMapper.toEntity(request, userId);
        session.setStatus(SpeakingSessionStatus.INIT);

        // TODO: Calling API to generate questions
        // Convert to DTO
        // Save to DBs (questions)
        // Session update status

        speakingSessionRepository.save(session);
        return speakingSessionMapper.toDto(session);
    }
}
