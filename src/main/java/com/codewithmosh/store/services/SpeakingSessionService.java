/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.speaking.*;
import com.codewithmosh.store.dtos.speaking.evaluation.EvaluationDto;
import com.codewithmosh.store.entities.SpeakingEvaluation;
import com.codewithmosh.store.entities.SpeakingQuestion;
import com.codewithmosh.store.entities.SpeakingResponse;
import com.codewithmosh.store.entities.SpeakingSession;
import com.codewithmosh.store.entities.enums.ScoreStatus;
import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import com.codewithmosh.store.exceptions.InvalidOperationException;
import com.codewithmosh.store.exceptions.ResourceNotFoundException;
import com.codewithmosh.store.exceptions.SpeakingSessionNotFoundException;
import com.codewithmosh.store.mapppers.SpeakingEvaluationMapper;
import com.codewithmosh.store.mapppers.SpeakingQuestionMapper;
import com.codewithmosh.store.mapppers.SpeakingResponseMapper;
import com.codewithmosh.store.mapppers.SpeakingSessionMapper;
import com.codewithmosh.store.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SpeakingSessionService {
    private final SpeakingSessionMapper speakingSessionMapper;
    private final SpeakingQuestionMapper speakingQuestionMapper;
    private final SpeakingResponseMapper speakingResponseMapper;
    private final SpeakingEvaluationMapper speakingEvaluationMapper;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final SpeakingSessionRepository speakingSessionRepository;
    private final SpeakingQuestionRepository speakingQuestionRepository;
    private final SpeakingResponseRepository speakingResponseRepository;
    private final SpeakingEvaluationRepository speakingEvaluationRepository;

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

    public SpeakingSessionDto getSession(Long id, Long userId) {
        SpeakingSession session = getAuthorizedSession(id, userId);
        return speakingSessionMapper.toDto(session);
    }

    public List<SpeakingQuestionDto> getQuestionsBySessionId(Long sessionId, Long userId) {
        SpeakingSession session = getAuthorizedSession(sessionId, userId);

        List<SpeakingQuestion> questions = speakingQuestionRepository.findBySpeakingSessionId(session.getId());

        return questions.stream()
                .map(speakingQuestionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SpeakingQuestionDto> getQuestionsByFilters(
            Long sessionId,
            @Nullable SpeakingPart part,
            @Nullable Long questionId,
            @Nullable Integer order,
            Long userId
    ) {
        SpeakingSession session = getAuthorizedSession(sessionId, userId);

        List<SpeakingQuestion> questions = speakingQuestionRepository.findByFilters(session.getId(), part, questionId, order);

        // Get all question IDs
        List<Long> questionIds = questions.stream()
                .map(SpeakingQuestion::getId)
                .collect(Collectors.toList());

        // Fetch all responses for these questions in a single query
        List<SpeakingResponse> responses = speakingResponseRepository.findBySessionIdAndQuestionIdIn(sessionId, questionIds);

        // Create a map of questionId -> response for efficient lookup
        Map<Long, SpeakingResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(
                        response -> response.getQuestion().getId(),
                        response -> response
                ));

        // Map questions to DTOs and include responses where available
        return questions.stream()
                .map(question -> {
                    SpeakingQuestionDto dto = speakingQuestionMapper.toDto(question);
                    // If there's a response for this question, add it to the DTO
                    SpeakingResponse response = responseMap.get(question.getId());
                    if (response != null) {
                        dto.setResponse(speakingResponseMapper.toDto(response));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public void updateSessionResponses(UpdateSpeakingSessionResponsesRequest request, Long sessionId, Long userId) {
        SpeakingSession session = getAuthorizedSession(sessionId, userId);

//        List<SpeakingResponse> updatedResponses = new ArrayList<>();

        for (SpeakingResponseRequestDto responseRequest : request.getResponses()) {
            Long questionId = responseRequest.getQuestionId();

            // Verify the question exists and belongs to the session
            SpeakingQuestion question = speakingQuestionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

            if (!question.getSpeakingSession().getId().equals(sessionId)) {
                throw new InvalidOperationException("Question does not belong to the specified session");
            }

            // Check if a response already exists for this question
            Optional<SpeakingResponse> existingResponse = speakingResponseRepository
                    .findBySessionIdAndQuestionId(sessionId, questionId);

            SpeakingResponse responseEntity;

            if (existingResponse.isPresent()) {
                // Update existing response
                responseEntity = existingResponse.get();
                responseEntity.setAudioUrl(responseRequest.getAudioUrl());
                responseEntity.setTranscript(responseRequest.getTranscript());
            } else {
                // Create new response
                responseEntity = speakingResponseMapper.toEntity(responseRequest);
                responseEntity.setQuestion(question);
                responseEntity.setSession(session);
            }
            speakingResponseRepository.save(responseEntity);
        }

        // Update session status if needed
        if (session.getStatus() == SpeakingSessionStatus.CREATED) {
            session.setStatus(SpeakingSessionStatus.IN_PROGRESS);
            speakingSessionRepository.save(session);
        }

    }
    /**
     * Score a speaking session
     * @param id the session id to be scored
     * @return the evaluation result as DTO
     */
    /**
     * Score a speaking session
     * @param id the session id to be scored
     * @return the evaluation result
     */
    public SpeakingEvaluationDto scoreSession(Long id, Long userId) {
        SpeakingSession session = getAuthorizedSession(id, userId);

        // Check if session is in a valid state for scoring
        if (session.getStatus() != SpeakingSessionStatus.IN_PROGRESS) {
            throw new InvalidOperationException("Session must be in IN_PROGRESS status to be scored");
        }

        // Get all questions and responses for the session
        List<SpeakingQuestionDto> questionsAndResponses = getQuestionsByFilters(id, null, null, null, userId);

        // Check if evaluation already exists
        Optional<SpeakingEvaluation> existingEvaluation =
                speakingEvaluationRepository.findBySpeakingSessionId(id);

        // If evaluation exists and is already scored, return it
        if (existingEvaluation.isPresent() && existingEvaluation.get().getScoreStatus() == ScoreStatus.SCORED) {
            return speakingEvaluationMapper.toDto(existingEvaluation.get());
        }

        // Call AI service to evaluate the session
        EvaluationDto evaluationDto = aiService.evaluateSpeakingSession(questionsAndResponses);

        // Create or update the evaluation entity
        SpeakingEvaluation evaluation;
        if (existingEvaluation.isPresent()) {
            evaluation = existingEvaluation.get();
        } else {
            evaluation = new SpeakingEvaluation();
            evaluation.setSpeakingSession(session);
        }

        // Extract the overall score from the evaluation DTO
        Double overallScore = evaluationDto.getOverview().getTotalScore();

        // Convert the evaluation DTO to a Map for storage
        try {
            Map<String, Object> scoreDetail = objectMapper.convertValue(evaluationDto, Map.class);

            // Update the evaluation entity
            evaluation.setScore(overallScore);
            evaluation.setScoreDetail(scoreDetail);
            evaluation.setScoreStatus(ScoreStatus.SCORED);

            // Update session status
            session.setStatus(SpeakingSessionStatus.SCORED);
            speakingSessionRepository.save(session);

            // Save the evaluation
            SpeakingEvaluation savedEvaluation = speakingEvaluationRepository.save(evaluation);

            // Map to DTO and return
            return speakingEvaluationMapper.toDto(savedEvaluation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert evaluation to JSON: " + e.getMessage(), e);
        }
    }
    /**
     * Get an evaluation by session ID
     * @param sessionId the session ID
     * @return the evaluation result
     */
    public SpeakingEvaluationDto getEvaluationBySessionId(Long sessionId, Long userId) {
        SpeakingSession session = getAuthorizedSession(sessionId, userId);
        SpeakingEvaluation evaluation = speakingEvaluationRepository.findBySpeakingSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found for session: " + sessionId));

        return speakingEvaluationMapper.toDto(evaluation);
    }

    public List<SpeakingSessionDto> getSessions(Long userId) {
        List<SpeakingSession> sessions = speakingSessionRepository.findByUserId(userId);

        return sessions.stream()
                .map(speakingSessionMapper::toDto)
                .collect(Collectors.toList());
    }
    private SpeakingSession getAuthorizedSession(Long sessionId, Long userId) {
        SpeakingSession session = speakingSessionRepository.findById(sessionId)
                .orElseThrow(SpeakingSessionNotFoundException::new);

        if (!session.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this session.");
        }

        return session;
    }

}
