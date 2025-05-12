/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services.ai;


import com.codewithmosh.store.dtos.ai.UsageDTO;
import com.codewithmosh.store.services.JsonSchemaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic service for generating responses based on JSON schemas
 */
@Service
public class SchemaBasedPromptService {

    private static final Logger logger = LoggerFactory.getLogger(SchemaBasedPromptService.class);
    private final OpenAiService openAiService;
    private final JsonSchemaService jsonSchemaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    // Tăng số lượng token tối đa
    @Value("${openai.max-tokens:4000}")
    private Integer defaultMaxTokens;

    // Giảm temperature để có phản hồi ngắn gọn và nhất quán hơn
    @Value("${openai.temperature:0.1}")
    private Double defaultTemperature;

    @Autowired
    public SchemaBasedPromptService(OpenAiService openAiService, JsonSchemaService jsonSchemaService) {
        this.openAiService = openAiService;
        this.jsonSchemaService = jsonSchemaService;
    }

    /**
     * Generate a response based on a JSON schema
     */
    public <T> T generateResponse(String prompt, String systemInstruction,
                                  String schemaJson, Class<T> responseClass) {
        try {
            logger.info("Generating schema-based response");

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(schemaJson);

            // Tạo hướng dẫn hệ thống với giới hạn độ dài rõ ràng
            String enhancedSystemInstruction = systemInstruction + "\n\n" +
                    "IMPORTANT: Keep your responses concise. Limit text fields to 2-3 sentences where possible. " +
                    "For array items, limit to 3-5 items maximum.\n\n" +
                    jsonSchemaService.createSchemaInstruction(schema);

            // Create the messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", enhancedSystemInstruction));
            messages.add(new ChatMessage("user", prompt));

            // Create the request with increased max tokens
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(defaultModel)
                    .messages(messages)
                    .temperature(defaultTemperature)
                    .maxTokens(defaultMaxTokens)
                    .build();

            // Execute the request
            ChatCompletionResult result = openAiService.createChatCompletion(request);

            // Get the response content
            String content = result.getChoices().get(0).getMessage().getContent();

            // Extract JSON content if wrapped in markdown code blocks
            content = extractJsonContent(content);

            // Kiểm tra và sửa chữa JSON không hoàn chỉnh
            content = ensureValidJson(content);

            // Parse the response into the DTO
            T responseDto = objectMapper.readValue(content, responseClass);

            // Try to set usage information if the DTO has the appropriate methods
            try {
                // Create usage DTO
                UsageDTO usage = new UsageDTO(
                        result.getUsage().getPromptTokens(),
                        result.getUsage().getCompletionTokens(),
                        result.getUsage().getTotalTokens()
                );

                // Try to find and invoke setUsage method
                try {
                    Method setUsageMethod = responseClass.getMethod("setUsage", UsageDTO.class);
                    setUsageMethod.invoke(responseDto, usage);
                } catch (NoSuchMethodException e) {
                    logger.debug("Response class does not have setUsage method");
                }

                // Try to find and invoke setModel method
                try {
                    Method setModelMethod = responseClass.getMethod("setModel", String.class);
                    setModelMethod.invoke(responseDto, defaultModel);
                } catch (NoSuchMethodException e) {
                    logger.debug("Response class does not have setModel method");
                }
            } catch (Exception e) {
                logger.warn("Could not set usage information on response DTO", e);
            }

            return responseDto;
        } catch (Exception e) {
            logger.error("Error generating schema-based response", e);
            throw new RuntimeException("Failed to generate schema-based response: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a response based on a JSON schema and return it as a JsonNode
     */
    public JsonNode generateRawResponse(String prompt, String systemInstruction, String schemaJson) {
        try {
            logger.info("Generating raw schema-based response");

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(schemaJson);

            // Tạo hướng dẫn hệ thống với giới hạn độ dài rõ ràng
            String enhancedSystemInstruction = systemInstruction + "\n\n" +
                    "IMPORTANT: Keep your responses concise. Limit text fields to 2-3 sentences where possible. " +
                    "For array items, limit to 3-5 items maximum.\n\n" +
                    jsonSchemaService.createSchemaInstruction(schema);

            // Create the messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", enhancedSystemInstruction));
            messages.add(new ChatMessage("user", prompt));

            // Create the request with increased max tokens
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(defaultModel)
                    .messages(messages)
                    .temperature(defaultTemperature)
                    .maxTokens(defaultMaxTokens)
                    .build();

            // Execute the request
            ChatCompletionResult result = openAiService.createChatCompletion(request);

            // Get the response content
            String content = result.getChoices().get(0).getMessage().getContent();

            // Extract JSON content if wrapped in markdown code blocks
            content = extractJsonContent(content);

            // Kiểm tra và sửa chữa JSON không hoàn chỉnh
            content = ensureValidJson(content);

            // Parse the response into a JsonNode
            JsonNode responseNode = objectMapper.readTree(content);

            // Add token usage information
            if (responseNode.isObject()) {
                ObjectNode objectNode = (ObjectNode) responseNode;

                // Add model information
                objectNode.put("model", defaultModel);

                // Add token usage information
                ObjectNode usageNode = objectMapper.createObjectNode();
                usageNode.put("input_tokens", result.getUsage().getPromptTokens());
                usageNode.put("output_tokens", result.getUsage().getCompletionTokens());
                usageNode.put("total_tokens", result.getUsage().getTotalTokens());
                objectNode.set("usage", usageNode);
            }

            return responseNode;
        } catch (Exception e) {
            logger.error("Error generating raw schema-based response", e);
            throw new RuntimeException("Failed to generate raw schema-based response: " + e.getMessage(), e);
        }
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

    /**
     * Ensure the JSON is valid and complete
     */
    private String ensureValidJson(String jsonContent) {
        try {
            // Thử parse JSON để kiểm tra tính hợp lệ
            objectMapper.readTree(jsonContent);
            return jsonContent; // Nếu không có lỗi, trả về nguyên bản
        } catch (IOException e) {
            logger.warn("Received incomplete JSON. Attempting to fix...");

            // Kiểm tra xem JSON có bị cắt giữa chừng không
            if (jsonContent.trim().endsWith(",")) {
                // Nếu kết thúc bằng dấu phẩy, thêm đóng ngoặc phù hợp
                return jsonContent + " null]}";
            }

            // Đếm số ngoặc mở và đóng
            int openBraces = countOccurrences(jsonContent, '{');
            int closeBraces = countOccurrences(jsonContent, '}');
            int openBrackets = countOccurrences(jsonContent, '[');
            int closeBrackets = countOccurrences(jsonContent, ']');

            // Thêm ngoặc đóng nếu cần
            StringBuilder fixedJson = new StringBuilder(jsonContent);
            for (int i = 0; i < openBraces - closeBraces; i++) {
                fixedJson.append("}");
            }
            for (int i = 0; i < openBrackets - closeBrackets; i++) {
                fixedJson.append("]");
            }

            // Kiểm tra lại JSON đã sửa
            try {
                objectMapper.readTree(fixedJson.toString());
                logger.info("Successfully fixed incomplete JSON");
                return fixedJson.toString();
            } catch (IOException ex) {
                logger.error("Could not fix incomplete JSON", ex);
                throw new RuntimeException("Received incomplete JSON that could not be fixed: " + ex.getMessage());
            }
        }
    }

    /**
     * Count occurrences of a character in a string
     */
    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
