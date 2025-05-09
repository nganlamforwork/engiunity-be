/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.services.ai.SchemaBasedPromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/schema")
public class SchemaBasedPromptController {

    private final SchemaBasedPromptService schemaBasedPromptService;

    @Autowired
    public SchemaBasedPromptController(SchemaBasedPromptService schemaBasedPromptService) {
        this.schemaBasedPromptService = schemaBasedPromptService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Object> generateSchemaBasedResponse(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        String systemInstruction = (String) request.get("systemInstruction");
        String schema = (String) request.get("schema");

        // Use Object.class as we don't know the exact type at compile time
        Object response = schemaBasedPromptService.generateResponse(
                prompt, systemInstruction, schema, Object.class);

        return ResponseEntity.ok(response);
    }
}
