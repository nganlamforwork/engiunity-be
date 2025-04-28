/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.controllers.ai;

import com.codewithmosh.store.services.ai.PromptTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final PromptTemplateService templateService;

    @Autowired
    public TemplateController(PromptTemplateService templateService) {
        this.templateService = templateService;
    }

//    @PostMapping("/{templateName}")
//    public ResponseEntity<Map<String, String>> generateFromTemplate(
//            @PathVariable String templateName,
//            @RequestBody Map<String, Object> request) {
//
//        @SuppressWarnings("unchecked")
//        Map<String, String> variables = (Map<String, String>) request.get("variables");
//        String systemInstruction = (String) request.get("systemInstruction");
//
//        String response = templateService.generateFromTemplate(templateName, variables, systemInstruction);
//
//        return ResponseEntity.ok(Map.of("response", response));
//    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addTemplate(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String template = request.get("template");

        templateService.addTemplate(name, template);

        return ResponseEntity.ok(Map.of("message", "Template added successfully"));
    }
}
