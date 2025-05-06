package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.dtos.vocabulary.*;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.repositories.VocabularyRepository;
import com.codewithmosh.store.services.vocabulary.VocabularyAIService;
import com.codewithmosh.store.services.vocabulary.VocabularyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/vocabulary")
@Tag(name = "Vocabulary AI", description = "AI-powered vocabulary learning APIs")
public class VocabularyAIController {
    private final VocabularyAIService vocabularyAIService;
    private final VocabularyService vocabularyService;
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestBody CreateSessionRequest request) {
        Long userId = request.getUserId();

        vocabularyService.createSession(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hunt")
    public ResponseEntity<VocabularyHuntDto> generateVocabularyWords(@RequestBody VocabularyHuntRequest request) {
        String topic = request.getTopic();
        String level = request.getLevel();
        int wordCount = request.getWordCount();
        Long sessionId = request.getSessionId();

        Long userId = userRepository.findBySessionId(sessionId);
        Set<String> excludedWords = vocabularyRepository.findAllWordsByUserId(userId);
        VocabularyAIResponseDto
                response = vocabularyAIService.generateVocabularyWords(topic, level, wordCount, excludedWords);
        vocabularyService.saveVocabularyGeneration(response, topic, level, sessionId);
        VocabularyHuntDto res = new VocabularyHuntDto();
        res.setData(response.getData());

        return ResponseEntity.ok(res);
    }

    @PostMapping("/paragraph")
    public ResponseEntity<ParagraphDto> generateParagraph(@RequestBody ParagraphRequest request) {
        Long sessionId = request.getSessionId();

        ParagraphDto paragraph = vocabularyService.generateAndSaveParagraph(sessionId);
        return ResponseEntity.ok(paragraph);
    }

    @PostMapping("/reading")
    public ResponseEntity<?> generateFeedback(@RequestBody ReadingRequest request) {
        vocabularyService.readingPractice(request.getSessionId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackDto> generateFeedback(@RequestBody FeedbackRequest request) {
        Long sessionId = request.getSessionId();
        String writing = request.getWriting();

        FeedbackDto paragraph = vocabularyService.generateAndSaveFeedback(sessionId, writing);
        return ResponseEntity.ok(paragraph);
    }
}
