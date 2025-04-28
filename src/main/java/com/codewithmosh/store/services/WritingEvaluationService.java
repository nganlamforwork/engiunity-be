/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.codewithmosh.store.dtos.ai.UsageDTO;
import com.codewithmosh.store.dtos.scoring.writing.WritingEvaluationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class WritingEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(WritingEvaluationService.class);
    private final OpenAiService openAiService;
    private final JsonSchemaService jsonSchemaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    // Tăng số lượng token tối đa lên đáng kể để đảm bảo phản hồi hoàn chỉnh
    @Value("${openai.max-tokens:4000}")
    private Integer defaultMaxTokens;

    // Giảm temperature để có phản hồi ngắn gọn và nhất quán hơn
    @Value("${openai.temperature:0.1}")
    private Double defaultTemperature;

    // Thời gian timeout cho API call
    @Value("${openai.timeout:120}")
    private Integer timeoutSeconds;

    private final String writingEvaluationSchema = """
            {
              "type": "object",
              "properties": {
                "overview": {
                  "type": "object",
                  "properties": {
                    "totalScore": { "type": "integer" },
                    "totalFeedback": { "type": "string" },
                    "overallImprovementSuggestion": { "type": "string" }
                  },
                  "required": ["totalScore", "totalFeedback", "overallImprovementSuggestion"]
                },
                "task_response": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer" },
                    "feedback": { "type": "string" },
                    "corrections": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "error": { "type": "string" },
                          "start_position": { "type": "integer" },
                          "end_position": { "type": "integer" },
                          "suggestion": { "type": "string" }
                        },
                        "required": ["error", "start_position", "end_position", "suggestion"]
                      }
                    },
                    "improvementSuggestion": { "type": "string" }
                  },
                  "required": ["score", "feedback", "corrections", "improvementSuggestion"]
                },
                "coherence_and_cohesion": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer" },
                    "feedback": { "type": "string" },
                    "corrections": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "error": { "type": "string" },
                          "start_position": { "type": "integer" },
                          "end_position": { "type": "integer" },
                          "suggestion": { "type": "string" }
                        },
                        "required": ["error", "start_position", "end_position", "suggestion"]
                      }
                    },
                    "improvementSuggestion": { "type": "string" }
                  },
                  "required": ["score", "feedback", "corrections", "improvementSuggestion"]
                },
                "lexical_resource": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer" },
                    "feedback": { "type": "string" },
                    "corrections": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "error": { "type": "string" },
                          "start_position": { "type": "integer" },
                          "end_position": { "type": "integer" },
                          "suggestion": { "type": "string" }
                        },
                        "required": ["error", "start_position", "end_position", "suggestion"]
                      }
                    },
                    "improvementSuggestion": { "type": "string" }
                  },
                  "required": ["score", "feedback", "corrections", "improvementSuggestion"]
                },
                "grammatical_range_and_accuracy": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer" },
                    "feedback": { "type": "string" },
                    "corrections": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "error": { "type": "string" },
                          "start_position": { "type": "integer" },
                          "end_position": { "type": "integer" },
                          "suggestion": { "type": "string" }
                        },
                        "required": ["error", "start_position", "end_position", "suggestion"]
                      }
                    },
                    "improvementSuggestion": { "type": "string" }
                  },
                  "required": ["score", "feedback", "corrections", "improvementSuggestion"]
                }
              },
              "required": [
                "overview",
                "task_response",
                "coherence_and_cohesion",
                "lexical_resource",
                "grammatical_range_and_accuracy"
              ]
            }
            """;

    @Autowired
    public WritingEvaluationService(OpenAiService openAiService, JsonSchemaService jsonSchemaService) {
        // Tạo OpenAiService với timeout tùy chỉnh
        this.openAiService = openAiService;
        this.jsonSchemaService = jsonSchemaService;
    }

    /**
     * Evaluate a writing sample using the predefined schema
     */
    public WritingEvaluationDTO evaluateWriting(String writingSample, String taskDescription) {
        try {
            logger.info("Evaluating writing sample");

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(writingEvaluationSchema);

            // Tạo hướng dẫn hệ thống với giới hạn độ dài rõ ràng
            String systemInstruction = "You are an expert writing evaluator for academic English tests. " +
                    "Evaluate the writing sample according to the IELTS scoring criteria: Task Response, " +
                    "Coherence and Cohesion, Lexical Resource, and Grammatical Range and Accuracy. " +
                    "Each category should be scored from 1 to 9.\n\n" +
                    "IMPORTANT: Keep your feedback concise. Limit each feedback section to 2-3 sentences. " +
                    "Limit each improvement suggestion to 1-2 sentences. " +
                    "For corrections, identify no more than 3 key issues per category.\n\n" +
                    jsonSchemaService.createSchemaInstruction(schema);

            // Create the user prompt
            String prompt = "Task Description: " + taskDescription + "\n\n" +
                    "Writing Sample:\n" + writingSample + "\n\n" +
                    "Please evaluate this writing sample and provide detailed feedback, corrections, and improvement suggestions.";

            // Create the messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemInstruction));
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
            WritingEvaluationDTO evaluationDTO = objectMapper.readValue(content, WritingEvaluationDTO.class);

            // Set the model used
            evaluationDTO.setModel(defaultModel);

            // Set token usage information
            TokenUsageDTO usage = new TokenUsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );
            evaluationDTO.setUsage(usage);

            return evaluationDTO;
        } catch (Exception e) {
            logger.error("Error evaluating writing sample", e);
            throw new RuntimeException("Failed to evaluate writing sample: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate a writing sample using a custom schema
     */
    public <T> T evaluateWritingWithCustomSchema(String writingSample, String taskDescription,
                                                 String customSchema, Class<T> responseClass) {
        try {
            logger.info("Evaluating writing sample with custom schema");

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(customSchema);

            // Create system instructions with clear length limits
            String systemInstruction = "You are an expert writing evaluator for academic English tests. " +
                    "Evaluate the writing sample according to the IELTS scoring criteria: Task Response, " +
                    "Coherence and Cohesion, Lexical Resource, and Grammatical Range and Accuracy. " +
                    "Each category must be scored from 1 to 9.\n\n" +

                    "IMPORTANT: Keep feedback concise. Limit each feedback section to 2-3 sentences. " +
                    "Limit each improvement suggestion to 1-2 sentences.\n\n" +

                    "When identifying mistakes, focus on major and representative errors only. " +
                    "Do NOT list every minor mistake. Select key errors that best reflect common or serious issues.\n\n" +

                    "If a particular type of error (e.g., verb tense, article usage, etc.) appears more than 5 times, " +
                    "include a WARNING in the overall evaluation noting that there are frequent and significant problems " +
                    "with that aspect of writing.\n\n" +

                    jsonSchemaService.createSchemaInstruction(schema);


            // Create the user prompt
            String prompt = "Task Description: " + taskDescription + "\n\n" +
                    "Writing Sample:\n" + writingSample + "\n\n" +
                    "Please evaluate this writing sample and provide detailed feedback according to the specified format.";

            // Create the messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemInstruction));
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
            content = ensureValidJson(content);

            // Parse the response into the DTO
            return objectMapper.readValue(content, responseClass);
        } catch (Exception e) {
            logger.error("Error evaluating writing sample with custom schema", e);
            throw new RuntimeException("Failed to evaluate writing sample with custom schema: " + e.getMessage(), e);
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
            // Try parsing JSON
            objectMapper.readTree(jsonContent);
            return jsonContent;
        } catch (IOException e) {
            logger.warn("Received incomplete JSON. Attempting to fix...");

            // if JSON ends with ,
            if (jsonContent.trim().endsWith(",")) {
                return jsonContent + " null]}";
            }

            // Count braces
            int openBraces = countOccurrences(jsonContent, '{');
            int closeBraces = countOccurrences(jsonContent, '}');
            int openBrackets = countOccurrences(jsonContent, '[');
            int closeBrackets = countOccurrences(jsonContent, ']');

            StringBuilder fixedJson = new StringBuilder(jsonContent);
            for (int i = 0; i < openBraces - closeBraces; i++) {
                fixedJson.append("}");
            }
            for (int i = 0; i < openBrackets - closeBrackets; i++) {
                fixedJson.append("]");
            }

            // Retry checking JSON
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
