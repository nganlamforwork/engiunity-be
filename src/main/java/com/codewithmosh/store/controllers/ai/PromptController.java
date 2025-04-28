/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.dtos.ai.ConversationResponseDTO;
import com.codewithmosh.store.dtos.ai.StructuredResponseDTO;
import com.codewithmosh.store.dtos.ai.TextResponseDTO;
import com.codewithmosh.store.services.ai.PromptEngineeringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prompt")
public class PromptController {

    private final PromptEngineeringService promptService;

    @Autowired
    public PromptController(PromptEngineeringService promptService) {
        this.promptService = promptService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TextResponseDTO> generateResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String systemInstruction = request.get("systemInstruction");

        TextResponseDTO response = promptService.generateTextResponse(prompt, systemInstruction);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate/advanced")
    public ResponseEntity<TextResponseDTO> generateAdvancedResponse(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        String systemInstruction = (String) request.get("systemInstruction");
        String model = request.containsKey("model") ? (String) request.get("model") : "gpt-3.5-turbo";
        Integer maxTokens = request.containsKey("maxTokens") ? (Integer) request.get("maxTokens") : 500;
        Double temperature = request.containsKey("temperature") ? (Double) request.get("temperature") : 0.7;

        TextResponseDTO response = promptService.generateTextResponse(
                prompt, systemInstruction, model, maxTokens, temperature);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate/structured")
    public ResponseEntity<StructuredResponseDTO> generateStructuredResponse(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        String systemInstruction = (String) request.get("systemInstruction");
        String outputFormat = (String) request.get("outputFormat");

        StructuredResponseDTO response = promptService.generateDynamicResponse(
                prompt, systemInstruction, outputFormat);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/conversation")
    public ResponseEntity<ConversationResponseDTO> generateConversationResponse(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");

        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = request.containsKey("messages") ?
                (List<Map<String, String>>) request.get("messages") : new ArrayList<>();

        ConversationResponseDTO response = promptService.generateConversationResponse(messages, prompt);

        return ResponseEntity.ok(response);
    }
}
