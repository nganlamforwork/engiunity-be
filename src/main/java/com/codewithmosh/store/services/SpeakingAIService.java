/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.services;

import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.codewithmosh.store.dtos.speaking.AISpeakingSessionResponseDto;
import com.codewithmosh.store.dtos.speaking.SpeakingQuestionDto;
import com.codewithmosh.store.dtos.speaking.SpeakingResponseDto;
import com.codewithmosh.store.dtos.speaking.evaluation.EvaluationDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final String speakingSessionEvaluationSchema = """
            {
              "type": "object",
              "properties": {
                "overview": {
                  "type": "object",
                  "properties": {
                    "totalScore": {
                      "type": "number",
                      "minimum": 0,
                      "maximum": 9
                    },
                    "overallFeedback": {
                      "type": "string"
                    },
                    "overallImprovementSuggestion": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "totalScore",
                    "overallFeedback",
                    "overallImprovementSuggestion"
                  ]
                },
                "fluency_and_coherence": {
                  "type": "object",
                  "properties": {
                    "score": {
                      "type": "number",
                      "minimum": 0,
                      "maximum": 9
                    },
                    "feedback": {
                      "type": "string"
                    },
                    "examples": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "excerpt": {
                            "type": "string"
                          },
                          "comment": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "excerpt",
                          "comment"
                        ]
                      }
                    },
                    "improvementSuggestion": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "score",
                    "feedback",
                    "examples",
                    "improvementSuggestion"
                  ]
                },
                "lexical_resource": {
                  "type": "object",
                  "properties": {
                    "score": {
                      "type": "number",
                      "minimum": 0,
                      "maximum": 9
                    },
                    "feedback": {
                      "type": "string"
                    },
                    "examples": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "excerpt": {
                            "type": "string"
                          },
                          "comment": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "excerpt",
                          "comment"
                        ]
                      }
                    },
                    "improvementSuggestion": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "score",
                    "feedback",
                    "examples",
                    "improvementSuggestion"
                  ]
                },
                "grammatical_range_and_accuracy": {
                  "type": "object",
                  "properties": {
                    "score": {
                      "type": "number",
                      "minimum": 0,
                      "maximum": 9
                    },
                    "feedback": {
                      "type": "string"
                    },
                    "examples": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "excerpt": {
                            "type": "string"
                          },
                          "comment": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "excerpt",
                          "comment"
                        ]
                      }
                    },
                    "improvementSuggestion": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "score",
                    "feedback",
                    "examples",
                    "improvementSuggestion"
                  ]
                },
                "pronunciation": {
                  "type": "object",
                  "properties": {
                    "score": {
                      "type": "number",
                      "minimum": 0,
                      "maximum": 9
                    },
                    "feedback": {
                      "type": "string"
                    },
                    "examples": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "excerpt": {
                            "type": "string"
                          },
                          "comment": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "excerpt",
                          "comment"
                        ]
                      }
                    },
                    "improvementSuggestion": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "score",
                    "feedback",
                    "examples",
                    "improvementSuggestion"
                  ]
                }
              },
              "required": [
                "overview",
                "fluency_and_coherence",
                "lexical_resource",
                "grammatical_range_and_accuracy",
                "pronunciation"
              ]
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

    /**
     * Evaluate a speaking session based on questions and responses
     * @param questions List of SpeakingQuestionDto containing questions and responses
     * @return Evaluation results following the IELTS speaking assessment criteria
     */
    public EvaluationDto evaluateSpeakingSession(List<SpeakingQuestionDto> questions) {
        try {
            logger.info("Evaluating speaking session with {} questions", questions.size());

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(speakingSessionEvaluationSchema);

            String systemInstruction = """
You are a certified IELTS Speaking examiner with extensive experience.
Your task is to evaluate a candidate's speaking performance based on the provided questions and responses.

Follow these strict IELTS Speaking assessment criteria:
1. Fluency and Coherence (how smoothly they speak, use of connectors, hesitations)
2. Lexical Resource (vocabulary range and accuracy)
3. Grammatical Range and Accuracy (sentence structures, tenses, errors)

For each criterion:
- Provide a band score from 0-9 (can use 0.5 increments)
- Give specific feedback with examples from the transcript
- Suggest concrete improvements

Guidelines for your evaluation:
- Be fair, objective, and consistent with official IELTS standards
- Provide specific examples from the candidate's responses to justify your scores
- Consider the speaking part requirements (Part 1, 2, or 3) in your assessment
- Highlight both strengths and areas for improvement
- Provide actionable advice that will help the candidate improve

Note: As this evaluation is based solely on the transcript, pronunciation should not be assessed at this time.

Return your evaluation in valid JSON according to this schema:
""" + jsonSchemaService.createSchemaInstruction(schema);

            // Format the questions and responses for the prompt
            String formattedData = formatQuestionsForPrompt(questions);

            // Create the user prompt
            String prompt = "Speaking Session Data:\n\n" + formattedData +
                    "\n\nPlease evaluate this speaking performance according to IELTS criteria.";

            // Create the messages
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemInstruction));
            messages.add(new ChatMessage("user", prompt));

            // Create the request
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

            // Extract and validate JSON content
            content = jsonValidatingService.extractJsonContent(content);
            content = jsonValidatingService.ensureValidJson(content);

            // Parse the response into the DTO
            EvaluationDto evaluationDto = objectMapper.readValue(content, EvaluationDto.class);

            // Set the model used
            evaluationDto.setModel(defaultModel);

            // Set token usage information
            TokenUsageDTO usage = new TokenUsageDTO(
                    result.getUsage().getPromptTokens(),
                    result.getUsage().getCompletionTokens(),
                    result.getUsage().getTotalTokens()
            );
            evaluationDto.setUsage(usage);

            return evaluationDto;
        } catch (Exception e) {
            logger.error("Error evaluating speaking session", e);
            throw new RuntimeException("Failed to evaluate speaking session: " + e.getMessage(), e);
        }
    }

    /**
     * Format questions and responses into a structured string for the AI prompt
     * @param questions List of SpeakingQuestionDto containing questions and responses
     * @return Formatted string representation of the questions and responses
     */
    private String formatQuestionsForPrompt(List<SpeakingQuestionDto> questions) {
        StringBuilder sb = new StringBuilder();

        // Group questions by part
        Map<SpeakingPart, List<SpeakingQuestionDto>> questionsByPart = questions.stream()
                .collect(Collectors.groupingBy(SpeakingQuestionDto::getPart));

        // Process each part in order
        for (SpeakingPart part : SpeakingPart.values()) {
            List<SpeakingQuestionDto> partQuestions = questionsByPart.get(part);
            if (partQuestions == null || partQuestions.isEmpty()) {
                continue;
            }

            sb.append("## ").append(part).append("\n\n");

            // Sort questions by order
            partQuestions.sort(Comparator.comparing(SpeakingQuestionDto::getOrder));

            for (SpeakingQuestionDto question : partQuestions) {
                // Format question
                sb.append("Question: ").append(question.getText()).append("\n");

                // Format sub-questions if present
                List<String> subQuestions = question.getSubQuestions();
                if (subQuestions != null && !subQuestions.isEmpty()) {
                    sb.append("Sub-questions:\n");
                    for (String subQ : subQuestions) {
                        sb.append("- ").append(subQ).append("\n");
                    }
                }

                // Format cue card if present
                List<String> cueCard = question.getCueCard();
                if (cueCard != null && !cueCard.isEmpty()) {
                    sb.append("Cue card:\n");
                    for (String cue : cueCard) {
                        sb.append("- ").append(cue).append("\n");
                    }
                }

                // Format follow-up if present
                List<String> followUp = question.getFollowUp();
                if (followUp != null && !followUp.isEmpty()) {
                    sb.append("Follow-up questions:\n");
                    for (String follow : followUp) {
                        sb.append("- ").append(follow).append("\n");
                    }
                }

                // Format response if present
                SpeakingResponseDto response = question.getResponse();
                if (response != null) {
                    sb.append("\nCandidate's response:\n");
                    sb.append(response.getTranscript()).append("\n");
                    sb.append("Audio URL: ").append(response.getAudioUrl()).append("\n");
                } else {
                    sb.append("\nNo response provided for this question.\n");
                }

                sb.append("\n---\n\n");
            }
        }

        return sb.toString();
    }
}
