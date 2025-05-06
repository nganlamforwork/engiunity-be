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
        templates.put("generate_vocabulary",new PromptTemplate(
                "Generate {{word_count}} vocabulary words for the topic \"{{topic}}\" at CEFR level {{level}}. " +
                        "For each word, provide the word itself, its type (noun, verb, adjective, etc.), " +
                        "the CEFR level, a list of 2-3 synonyms, and an example sentence with the word highlighted using ** (asterisks)."
        ));

        // Template for generating paragraphs with vocabulary words
        templates.put("generate_paragraph",new PromptTemplate(
                "Generate a coherent paragraph about \"{{topic}}\" using the following vocabulary words: {{word_list}}. " +
                        "Highlight each vocabulary word using ** (asterisks) when it appears in the paragraph."
        ));

        // Template for generating feedback on writing
        templates.put("generate_feedback",new PromptTemplate(
                "Provide feedback on this language learner's writing about \"{{topic}}\" at CEFR level {{level}}. " +
                        "The writing should use these vocabulary words: {{word_list}}.\n\n" +
                        "Here is the learner's writing:\n\"{{user_writing}}\"\n\n" +
                        "Also provide an improved version of the text that maintains the same ideas but uses the vocabulary words more effectively."
        ));

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
