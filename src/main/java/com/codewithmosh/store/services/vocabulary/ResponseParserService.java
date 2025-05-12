package com.codewithmosh.store.services.vocabulary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.StructuredResponseDTO;
import com.codewithmosh.store.dtos.ai.TextResponseDTO;
import com.codewithmosh.store.dtos.ai.UsageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResponseParserService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseParserService.class);
    private final ObjectMapper objectMapper;

    /**
     * Parse a text response from OpenAI
     */
    public TextResponseDTO parseTextResponse(String content, String model, ChatCompletionResult result) {
        UsageDTO usage = new UsageDTO(
                result.getUsage().getPromptTokens(),
                result.getUsage().getCompletionTokens(),
                result.getUsage().getTotalTokens()
        );

        return new TextResponseDTO(content, model, usage);
    }

    /**
     * Parse a structured response from OpenAI into a specific DTO class
     */
    public <T extends ResponseDTO> T parseStructuredResponse(String content, String model,
                                                             ChatCompletionResult result, Class<T> dtoClass) {
        try {
            T responseDTO = objectMapper.readValue(content, dtoClass);

//            // Set common fields
//            responseDTO.setModel(model);
//            responseDTO.setUsage(new UsageDTO(
//                    result.getUsage().getPromptTokens(),
//                    result.getUsage().getCompletionTokens(),
//                    result.getUsage().getTotalTokens()
//            ));

            return responseDTO;
        } catch (Exception e) {
            logger.error("Error parsing structured response", e);
            throw new RuntimeException("Failed to parse structured response from OpenAI", e);
        }
    }

    /**
     * Parse a dynamic structured response from OpenAI
     */
    public StructuredResponseDTO parseDynamicResponse(String content, String model, ChatCompletionResult result) {
        try {
            Object parsedContent;

            if (content.trim().startsWith("[")) {
                // It's a JSON array
                parsedContent = objectMapper.readValue(content, new TypeReference<Object[]>() {});
            } else {
                // It's a JSON object
                parsedContent = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
            }

            UsageDTO usage = new UsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );

            Map<String, Object> wrapped = new HashMap<>();
            wrapped.put("data", parsedContent);

            return new StructuredResponseDTO(model, usage, wrapped);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing dynamic response", e);
            throw new RuntimeException("Failed to parse dynamic response from OpenAI", e);
        }
    }
}
