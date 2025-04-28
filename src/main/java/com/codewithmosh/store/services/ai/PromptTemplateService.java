/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services.ai;

import com.codewithmosh.store.dtos.ai.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PromptTemplateService {

    private final PromptEngineeringService promptService;
    private final Map<String, PromptTemplate> templates = new HashMap<>();

    @Autowired
    public PromptTemplateService(PromptEngineeringService promptService) {
        this.promptService = promptService;
        initializeTemplates();
    }

    private void initializeTemplates() {
        // Add some common templates
        templates.put("summarize", new PromptTemplate(
                "Summarize the following text in {{length}} sentences:\n\n{{text}}"
        ));

        templates.put("translate", new PromptTemplate(
                "Translate the following text from {{source_language}} to {{target_language}}:\n\n{{text}}"
        ));

        templates.put("explain", new PromptTemplate(
                "Explain {{concept}} in simple terms that a {{audience}} would understand."
        ));
    }

    /**
     * Get a template by name
     *
     * @param name Template name
     * @return The template
     */
    public PromptTemplate getTemplate(String name) {
        if (!templates.containsKey(name)) {
            throw new IllegalArgumentException("Template not found: " + name);
        }
        return templates.get(name);
    }

    /**
     * Add a new template
     *
     * @param name Template name
     * @param template Template string
     */
    public void addTemplate(String name, String template) {
        templates.put(name, new PromptTemplate(template));
    }

    /**
     * Use a template to generate a response
     *
     * @param templateName Template name
     * @param variables Template variables
     * @param systemInstruction System instruction
     * @return The generated response
     */
//    public String generateFromTemplate(String templateName, Map<String, String> variables, String systemInstruction) {
//        PromptTemplate template = getTemplate(templateName);
//        String prompt = template.format(variables);
//        return promptService.generateResponse(prompt, systemInstruction);
//    }
}
