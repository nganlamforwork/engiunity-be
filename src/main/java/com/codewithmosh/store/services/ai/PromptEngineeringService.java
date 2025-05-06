/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services.ai;

import com.codewithmosh.store.dtos.ai.*;
import com.codewithmosh.store.services.vocabulary.ResponseParserService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PromptEngineeringService {

    private static final Logger logger = LoggerFactory.getLogger(PromptEngineeringService.class);

    private final OpenAiService openAiService;
    private final ResponseParserService responseParserService;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    @Value("${openai.maxTokens:3000}")
    private Integer defaultMaxTokens;

    @Value("${openai.temperature:0.4}")
    private Double defaultTemperature;

    @Autowired
    public PromptEngineeringService(OpenAiService openAiService, ResponseParserService responseParserService) {
        this.openAiService = openAiService;
        this.responseParserService = responseParserService;
        logger.info("PromptEngineeringService initialized with model: {}", defaultModel);
    }

    /**
     * Generate a text response
     */
    public TextResponseDTO generateTextResponse(String prompt, String systemInstruction) {
        return generateTextResponse(prompt, systemInstruction, defaultModel, defaultMaxTokens, defaultTemperature);
    }

    /**
     * Generate a text response with full customization options
     */
    public TextResponseDTO generateTextResponse(String prompt, String systemInstruction,
                                                String model, Integer maxTokens, Double temperature) {
        try {
            logger.debug("Generating text response for prompt: {}", prompt);

            ChatCompletionResult result = executeRequest(prompt, systemInstruction, model, maxTokens, temperature);
            String content = result.getChoices().get(0).getMessage().getContent();

            return responseParserService.parseTextResponse(content, model, result);
        } catch (Exception e) {
            logger.error("Error generating text response", e);
            throw new RuntimeException("Failed to generate text response from OpenAI", e);
        }
    }

    /**
     * Generate a structured response based on a JSON schema
     */
    public <T extends ResponseDTO> T generateStructuredResponse(String prompt, String systemInstruction,
                                                                String outputFormat, Class<T> dtoClass) {
        return generateStructuredResponse(prompt, systemInstruction, outputFormat,
                defaultModel, defaultMaxTokens, defaultTemperature, dtoClass);
    }

    /**
     * Generate a structured response with full customization options
     */
    public <T extends ResponseDTO> T generateStructuredResponse(String prompt, String systemInstruction,
                                                                String outputFormat, String model,
                                                                Integer maxTokens, Double temperature,
                                                                Class<T> dtoClass) {
        try {
            logger.debug("Generating structured response for prompt: {}", prompt);

            // Add output format instruction to the system message
            String enhancedSystemInstruction = systemInstruction + "\\n\\n" +
                    "You must respond in the following JSON format: " + outputFormat;

            ChatCompletionResult result = executeRequest(prompt, enhancedSystemInstruction, model, maxTokens, temperature);
            String content = result.getChoices().get(0).getMessage().getContent();

            // Extract JSON content if wrapped in markdown code blocks
            content = extractJsonContent(content);

            return responseParserService.parseStructuredResponse(content, model, result, dtoClass);
        } catch (Exception e) {
            logger.error("Error generating structured response", e);
            throw new RuntimeException("Failed to generate structured response from OpenAI", e);
        }
    }

    /**
     * Generate a dynamic structured response
     */
    public StructuredResponseDTO generateDynamicResponse(String prompt, String systemInstruction,
                                                         String outputFormat) {
        return generateDynamicResponse(prompt, systemInstruction, outputFormat,
                defaultModel, defaultMaxTokens, defaultTemperature);
    }

    /**
     * Generate a dynamic structured response with full customization options
     */
    public StructuredResponseDTO generateDynamicResponse(String prompt, String systemInstruction,
                                                         String outputFormat, String model,
                                                         Integer maxTokens, Double temperature) {
        try {
            logger.debug("Generating dynamic response for prompt: {}", prompt);

            // Add output format instruction to the system message
            String enhancedSystemInstruction = systemInstruction + "\\n\\n" +
                    "You must respond in the following JSON format: " + outputFormat;

            ChatCompletionResult result = executeRequest(prompt, enhancedSystemInstruction, model, maxTokens, temperature);
            String content = result.getChoices().get(0).getMessage().getContent();

            // Extract JSON content if wrapped in markdown code blocks
            content = extractJsonContent(content);

            return responseParserService.parseDynamicResponse(content, model, result);
        } catch (Exception e) {
            logger.error("Error generating dynamic response", e);
            throw new RuntimeException("Failed to generate dynamic response from OpenAI", e);
        }
    }

    /**
     * Generate a conversation response
     */
    public ConversationResponseDTO generateConversationResponse(List<Map<String, String>> messages, String newPrompt) {
        try {
            List<ChatMessage> chatMessages = new ArrayList<>();

            // Convert previous messages to ChatMessage format
            for (Map<String, String> message : messages) {
                chatMessages.add(new ChatMessage(message.get("role"), message.get("content")));
            }

            // Add the new user prompt
            chatMessages.add(new ChatMessage("user", newPrompt));

            // Create the request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(defaultModel)
                    .messages(chatMessages)
                    .temperature(defaultTemperature)
                    .maxTokens(defaultMaxTokens)
                    .build();

            // Execute the request
            ChatCompletionResult result = openAiService.createChatCompletion(request);

            String responseContent = result.getChoices().get(0).getMessage().getContent();

            // Add the assistant's response to the messages
            Map<String, String> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", responseContent);
            messages.add(assistantMessage);

            // Create usage DTO
            UsageDTO usage = new UsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );

            // Create and return the result
            return new ConversationResponseDTO(responseContent, defaultModel, messages, usage);
        } catch (Exception e) {
            logger.error("Error generating conversation response", e);
            throw new RuntimeException("Failed to generate conversation response from OpenAI", e);
        }
    }

    /**
     * Execute a request to the OpenAI API
     */
    private ChatCompletionResult executeRequest(String prompt, String systemInstruction,
                                                String model, Integer maxTokens, Double temperature) {
        List<ChatMessage> messages = new ArrayList<>();

        // Add system message if provided
        if (systemInstruction != null && !systemInstruction.isEmpty()) {
            messages.add(new ChatMessage("system", systemInstruction));
        }

        // Add user message
        messages.add(new ChatMessage("user", prompt));

        // Create the request
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        // Execute the request
        return openAiService.createChatCompletion(request);
    }

    /**
     * Extract JSON content from a string that might be wrapped in markdown code blocks
     */
    private String extractJsonContent(String content) {
        // Check if content is wrapped in markdown code blocks
        if (content.startsWith("```json") && content.endsWith("```")) {
            return content.substring(7, content.length() - 3).trim();
        } else if (content.startsWith("```") && content.endsWith("```")) {
            return content.substring(3, content.length() - 3).trim();
        }
        return content;
    }
}
