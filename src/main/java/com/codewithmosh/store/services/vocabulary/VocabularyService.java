package com.codewithmosh.store.services.vocabulary;

import com.codewithmosh.store.dtos.vocabulary.FeedbackDto;
import com.codewithmosh.store.dtos.vocabulary.ParagraphDto;
import com.codewithmosh.store.dtos.vocabulary.VocabularyAIResponseDto;
import com.codewithmosh.store.dtos.vocabulary.VocabularyItemDto;
import com.codewithmosh.store.entities.Feedback;
import com.codewithmosh.store.entities.Paragraph;
import com.codewithmosh.store.entities.Session;
import com.codewithmosh.store.entities.Vocabulary;
import com.codewithmosh.store.entities.enums.SessionStatus;
import com.codewithmosh.store.exceptions.InvalidStateException;
import com.codewithmosh.store.exceptions.ResourceNotFoundException;
import com.codewithmosh.store.mappers.FeedbackMapper;
import com.codewithmosh.store.mappers.ParagraphMapper;
import com.codewithmosh.store.mappers.VocabularyMapper;
import com.codewithmosh.store.repositories.FeedbackRepository;
import com.codewithmosh.store.repositories.ParagraphRepository;
import com.codewithmosh.store.repositories.SessionRepository;
import com.codewithmosh.store.repositories.VocabularyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VocabularyService {
    private static final Logger logger = LoggerFactory.getLogger(VocabularyService.class);

    private final VocabularyRepository vocabularyRepository;
    private final SessionRepository sessionRepository;
    private final ParagraphRepository paragraphRepository;
    private final FeedbackRepository feedbackRepository;
    private final VocabularyMapper vocabularyMapper;
    private final ParagraphMapper paragraphMapper;
    private final FeedbackMapper feedbackMapper;
    private final VocabularyAIService vocabularyAIService;

    @Autowired
    public VocabularyService(
            VocabularyRepository vocabularyRepository,
            SessionRepository sessionRepository,
            ParagraphRepository paragraphRepository,
            FeedbackRepository feedbackRepository,
            VocabularyMapper vocabularyMapper,
            ParagraphMapper paragraphMapper,
            FeedbackMapper feedbackMapper,
            VocabularyAIService vocabularyAIService) {
        this.vocabularyRepository = vocabularyRepository;
        this.sessionRepository = sessionRepository;
        this.paragraphRepository = paragraphRepository;
        this.feedbackRepository = feedbackRepository;
        this.vocabularyMapper = vocabularyMapper;
        this.paragraphMapper = paragraphMapper;
        this.feedbackMapper = feedbackMapper;
        this.vocabularyAIService = vocabularyAIService;
    }

    @Transactional
    public void createSession(Long userId) {
        logger.info("Saving session for user ID: {}", userId);

        // Create a new session
        Session session = new Session();
        session.setStatus(SessionStatus.WORDS_GENERATED.name());
        session.setUserId(userId);
        sessionRepository.save(session);
    }

    @Transactional
    public void saveVocabularyGeneration(VocabularyAIResponseDto vocabularyHuntDto, String topic, String level,
                                         Long sessionId) {
        logger.info("Saving vocabulary generation for topic: '{}' at level {}", topic, level);

        // Create a new session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
        if (!SessionStatus.WORDS_GENERATED.name().equals(session.getStatus())) {
            throw new InvalidStateException("Session must be in WORDS_GENERATED state to generate paragraphs");
        }
        session.setStatus(SessionStatus.PARAGRAPH_GENERATED.name());
        session.setTopic(topic);
        session = sessionRepository.save(session);

        for (VocabularyItemDto itemDto : vocabularyHuntDto.getData()) {
            // Use mapper to create entity
            Vocabulary vocabularyEntity = vocabularyMapper.toEntity(itemDto);

            // Set the session
            vocabularyEntity.setSession(session);

            // Save vocabulary entity
           vocabularyRepository.save(vocabularyEntity);
        }

    }

    @Transactional
    public ParagraphDto generateAndSaveParagraph(Long sessionId) {
        logger.info("Generating paragraph for session ID: {}", sessionId);

        // Find the session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        // Check if session is in the correct state
        if (!SessionStatus.PARAGRAPH_GENERATED.name().equals(session.getStatus())) {
            throw new InvalidStateException("Session must be in PARAGRAPH_GENERATED state to generate paragraphs");
        }

        // Get vocabulary words from the session
        List<Vocabulary> vocabularies;
        vocabularies = vocabularyRepository.findAllBySessionId(sessionId);
        if (vocabularies.isEmpty()) {
            throw new ResourceNotFoundException("No vocabulary words found for session ID: " + sessionId);
        }

        // Extract words for paragraph generation
        List<String> words = vocabularies.stream()
                .map(Vocabulary::getWord)
                .collect(Collectors.toList());

        // Get topic from session
        String topic = session.getTopic();
        if (topic == null || topic.isEmpty()) {
            throw new InvalidStateException("Session has no topic defined");
        }

        // Generate paragraph using AI service
        String generatedParagraph = vocabularyAIService.generateParagraph(topic, words);

        // Create and save paragraph entity
        Paragraph paragraph = new Paragraph();
        paragraph.setContent(generatedParagraph);
        paragraph.setSession(session);
        paragraph = paragraphRepository.save(paragraph);

        // Update session status
        session.setStatus(SessionStatus.READING.name());
        sessionRepository.save(session);

        return paragraphMapper.toDto(paragraph);
    }

    @Transactional
    public void readingPractice(Long sessionId) {
        logger.info("Starting reading practice for session ID: {}", sessionId);

        // Find the session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        // Check if session is in the correct state
        if (!SessionStatus.READING.name().equals(session.getStatus())) {
            throw new InvalidStateException("Session must be in READING state to start reading practice");
        }

        // Update session status
        session.setStatus(SessionStatus.FEEDBACK_GENERATED.name());
        sessionRepository.save(session);
    }

    @Transactional
    public FeedbackDto generateAndSaveFeedback(Long sessionId, String userWriting) {
        logger.info("Generating feedback for session ID: {}", sessionId);

        // Find the session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        // Check if session is in the correct state
        if (!SessionStatus.FEEDBACK_GENERATED.name().equals(session.getStatus())) {
            throw new InvalidStateException("Session must be in FEEDBACK_GENERATED state to generate feedback");
        }

        // Get vocabulary words
        List<Vocabulary> vocabularies = vocabularyRepository.findAllBySessionId(sessionId);
        if (vocabularies.isEmpty()) {
            throw new ResourceNotFoundException("No vocabulary words found for session ID: " + sessionId);
        }

        // Extract words for feedback generation
        List<String> words = vocabularies.stream()
                .map(Vocabulary::getWord)
                .collect(Collectors.toList());

        // Get topic from session
        String topic = session.getTopic();
        if (topic == null || topic.isEmpty()) {
            throw new InvalidStateException("Session has no topic defined");
        }

        // Get level from vocabulary (assuming all words have the same level)
        String level = vocabularies.getFirst().getLevel();

        // Generate feedback using AI service
        FeedbackDto aiGeneratedFeedback =
                vocabularyAIService.generateFeedback(userWriting, topic, level, words);

        // Create and save feedback entity
        Feedback feedback = new Feedback();
        feedback.setFeedback(aiGeneratedFeedback.getFeedback());
        feedback.setImprovedAnswer(aiGeneratedFeedback.getImprovedAnswer());
        feedback.setUserWriting(userWriting);
        feedback.setSession(session);
        feedback = feedbackRepository.save(feedback);

        // Update session status
        session.setStatus(SessionStatus.COMPLETED.name());
        sessionRepository.save(session);

        return feedbackMapper.toDto(feedback);
    }
}