/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.dtos.ai.CustomResponseDTO;
import com.codewithmosh.store.services.ai.PromptEngineeringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/custom")
public class CustomPromptController {

    private final PromptEngineeringService promptService;

    @Autowired
    public CustomPromptController(PromptEngineeringService promptService) {
        this.promptService = promptService;
    }

    @PostMapping("/question")
    public ResponseEntity<CustomResponseDTO> answerQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        // Define the output format for the custom response
        String outputFormat = """
                {
                  "answer": "The detailed answer to the question",
                  "confidence": 0.95,
                  "sources": ["source1", "source2"]
                }
                """;

        // Define the system instruction
        String systemInstruction = "You are a helpful assistant that provides detailed answers with confidence scores and sources.";

        // Generate the structured response
        CustomResponseDTO response = promptService.generateStructuredResponse(
                question, systemInstruction, outputFormat, CustomResponseDTO.class);

        return ResponseEntity.ok(response);
    }
}
