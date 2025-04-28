/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services.ai;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.StructuredResponseDTO;
import com.codewithmosh.store.dtos.ai.TextResponseDTO;
import com.codewithmosh.store.dtos.ai.UsageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for parsing OpenAI responses into structured DTOs
 */
@Service
public class ResponseParserService {

    private static final Logger logger = LoggerFactory.getLogger(ResponseParserService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse a text response into a TextResponseDTO
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
     * Parse a response into a structured DTO based on a JSON schema
     */
    public <T extends ResponseDTO> T parseStructuredResponse(String content, String model,
                                                             ChatCompletionResult result,
                                                             Class<T> dtoClass) {
        try {
            // Create the DTO instance
            T dto = objectMapper.readValue(content, dtoClass);

            // If it's a structured response, add model and usage info
            if (dto instanceof StructuredResponseDTO) {
                StructuredResponseDTO structuredDto = (StructuredResponseDTO) dto;
                structuredDto.setModel(model);

                UsageDTO usage = new UsageDTO(
                        result.getUsage().getPromptTokens(),
                        result.getUsage().getCompletionTokens(),
                        result.getUsage().getTotalTokens()
                );
                structuredDto.setUsage(usage);
            }

            return dto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse structured response", e);
            throw new RuntimeException("Failed to parse structured response: " + e.getMessage(), e);
        }
    }

    /**
     * Parse a response into a dynamic StructuredResponseDTO
     */
    public StructuredResponseDTO parseDynamicResponse(String content, String model, ChatCompletionResult result) {
        try {
            // Parse the content as a Map
            Map<String, Object> contentMap = objectMapper.readValue(content, Map.class);

            // Create a structured response DTO
            StructuredResponseDTO dto = new StructuredResponseDTO();
            dto.setModel(model);

            UsageDTO usage = new UsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );
            dto.setUsage(usage);

            // Add all properties from the content
            dto.setProperties(contentMap);

            return dto;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse dynamic response", e);
            throw new RuntimeException("Failed to parse dynamic response: " + e.getMessage(), e);
        }
    }
}
