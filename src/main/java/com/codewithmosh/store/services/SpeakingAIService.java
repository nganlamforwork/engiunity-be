/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.codewithmosh.store.dtos.speaking.AISpeakingSessionResponseDto;
import com.codewithmosh.store.dtos.speaking.SpeakingQuestionDto;
import com.codewithmosh.store.dtos.writing.scoring.WritingEvaluationDto;
import com.codewithmosh.store.entities.enums.SpeakingPart;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class SpeakingAIService {
    private static final Logger logger = LoggerFactory.getLogger(WritingAIService.class);
    private final OpenAiService openAiService;
    private final JsonSchemaService jsonSchemaService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonValidatingService jsonValidatingService;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    @Value("${openai.max-tokens:4000}")
    private Integer defaultMaxTokens;

    @Value("${openai.temperature:0.1}")
    private Double defaultTemperature;

    @Value("${openai.timeout:120}")
    private Integer timeoutSeconds;

    private final String speakingSessionCreationSchema = """
            {
              "type": "object",
              "properties": {
                "questions": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "part": {
                        "type": "string",
                        "enum": ["Part 1", "Part 2", "Part 3"]
                      },
                      "order": {
                        "type": "integer"
                      },
                      "text": {
                        "type": "string"
                      },
                      "subQuestions": {
                        "type": "array",
                        "items": {
                          "type": "string"
                        }
                      },
                      "cueCard": {
                        "type": "array",
                        "items": {
                          "type": "string"
                        }
                      },
                      "followUp": {
                        "type": "array",
                        "items": {
                          "type": "string"
                        }
                      }
                    },
                    "required": ["part", "order", "text"]
                  }
                }
              },
              "required": ["questions"]
            }
            """;

    @Autowired
    public SpeakingAIService(OpenAiService openAiService, JsonSchemaService jsonSchemaService, JsonValidatingService jsonValidatingService) {
        this.openAiService = openAiService;
        this.jsonSchemaService = jsonSchemaService;
        this.jsonValidatingService = jsonValidatingService;
    }

    /*
    * Creating speaking session using the predefined schema
    * */
    public AISpeakingSessionResponseDto createSpeakingSessionQuestions(String topic, String notes, SpeakingPart part) {
        try {
            logger.info("Creating questions for speaking session");

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(speakingSessionCreationSchema);

            String systemInstruction = """
You are a certified IELTS Speaking examiner.
Your task is to generate authentic IELTS Speaking questions for the given topic and part.

Follow these strict guidelines:
- Follow the IELTS Speaking format exactly.
- Ensure the questions are natural, clear, and appropriate for the level.
- Always base your questions on the topic provided.
- If detail aspects are provided, your questions must focus on those aspects specifically.
- If no aspects are provided, select one relevant aspect of the topic at random and focus your questions on that.
- For Part 1: Write 8–10 short, personal, general questions.
- For Part 2: Provide one long-form cue card with 2–3 bullet prompts and 1 follow-up question.
- For Part 3: Write 5–7 in-depth discussion questions based on the topic, with increasing complexity.
  - Additionally, include 2–3 *follow-up suggestion questions* to help learners practice extending their responses. These should be clearly labeled and separated.
- Avoid yes/no questions and questions that can be answered in one word.
- Do not repeat questions.

Return your response in valid JSON according to this schema:
""" + jsonSchemaService.createSchemaInstruction(schema);


            // Create the user prompt
            String prompt = "Topic: " + topic + "\n\n" +
                    "Detail aspects: " + notes + "\n\n" +
                    "Speaking Part: " + part + "\n\n" +
                    "Please generate IELTS Speaking questions that strictly follow the structure and rules for this part.";

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

            content = jsonValidatingService.extractJsonContent(content);

            content = jsonValidatingService.ensureValidJson(content);

            // Parse the response into the DTO
            AISpeakingSessionResponseDto speakingSessionResponseDto = objectMapper.readValue(content, AISpeakingSessionResponseDto.class);

            // Set the model used
            speakingSessionResponseDto.setModel(defaultModel);

            // Set token usage information
            TokenUsageDTO usage = new TokenUsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );
            speakingSessionResponseDto.setUsage(usage);

            return speakingSessionResponseDto;
        } catch (Exception e) {
            logger.error("Error creating speaking session questions", e);
            throw new RuntimeException("Failed to create speaking session questions: " + e.getMessage(), e);
        }
    }
}
