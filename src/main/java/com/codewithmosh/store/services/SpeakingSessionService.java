/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.speaking.AISpeakingSessionResponseDto;
import com.codewithmosh.store.dtos.speaking.CreateSpeakingSessionRequest;
import com.codewithmosh.store.dtos.speaking.SpeakingQuestionDto;
import com.codewithmosh.store.dtos.speaking.SpeakingSessionDto;
import com.codewithmosh.store.entities.SpeakingQuestion;
import com.codewithmosh.store.entities.SpeakingSession;
import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import com.codewithmosh.store.exceptions.SpeakingSessionNotFoundException;
import com.codewithmosh.store.mapppers.SpeakingQuestionMapper;
import com.codewithmosh.store.mapppers.SpeakingSessionMapper;
import com.codewithmosh.store.repositories.SpeakingQuestionRepository;
import com.codewithmosh.store.repositories.SpeakingSessionRepository;
import com.codewithmosh.store.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SpeakingSessionService {
    private final SpeakingSessionMapper speakingSessionMapper;
    private final SpeakingQuestionMapper speakingQuestionMapper;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final SpeakingSessionRepository speakingSessionRepository;
    private final SpeakingQuestionRepository speakingQuestionRepository;

    private final SpeakingAIService aiService;

    public SpeakingSessionDto createSession(CreateSpeakingSessionRequest request, Long userId) {
        SpeakingSession session = speakingSessionMapper.toEntity(request, userId);
        session.setStatus(SpeakingSessionStatus.INIT);
        SpeakingSession savedSession = speakingSessionRepository.save(session);

        AISpeakingSessionResponseDto aiResponse = aiService.createSpeakingSessionQuestions(
                request.getTopic(), request.getNotes(), request.getPart()
        );

        savedSession.setStatus(SpeakingSessionStatus.CREATED);
        speakingSessionRepository.save(savedSession);

        List<SpeakingQuestionDto> questionDtos = aiResponse.getQuestions();
        List<SpeakingQuestion> questions = questionDtos.stream()
                .map(speakingQuestionMapper::toEntity)
                .peek(q -> q.setSpeakingSession(savedSession))
                .collect(Collectors.toList());

        speakingQuestionRepository.saveAll(questions);
        savedSession.setQuestions(questions);
        return speakingSessionMapper.toDto(savedSession);
    }

    public SpeakingSessionDto getSession(Long id){
        SpeakingSession session = speakingSessionRepository.findById(id).orElse(null);
        if (session == null) {
            throw new SpeakingSessionNotFoundException();
        }
        return speakingSessionMapper.toDto(session);
    }

    public List<SpeakingQuestionDto> getQuestionsBySessionId(Long sessionId) {
        SpeakingSession session = speakingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SpeakingSessionNotFoundException());

        List<SpeakingQuestion> questions = speakingQuestionRepository.findBySpeakingSessionId(session.getId());

        return questions.stream()
                .map(speakingQuestionMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<SpeakingQuestionDto> getQuestionsByFilters(
            Long sessionId,
            @Nullable SpeakingPart part,
            @Nullable Long questionId,
            @Nullable Integer order
    ) {
        SpeakingSession session = speakingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SpeakingSessionNotFoundException());

        List<SpeakingQuestion> questions = speakingQuestionRepository.findByFilters(session.getId(), part, questionId, order);

        return questions.stream()
                .map(speakingQuestionMapper::toDto)
                .collect(Collectors.toList());
    }
}
