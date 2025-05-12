package com.codewithmosh.store.services.vocabulary;

import com.codewithmosh.store.dtos.vocabulary.FeedbackDto;
import com.codewithmosh.store.dtos.vocabulary.VocabularyAIResponseDto;
import com.codewithmosh.store.services.JsonSchemaService;
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
import java.util.Set;

@Service
public class VocabularyAIService {
    private static final Logger logger = LoggerFactory.getLogger(VocabularyAIService.class);
    private final OpenAiService openAiService;
    private final JsonSchemaService jsonSchemaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.model:gpt-3.5-turbo}")
    private String defaultModel;

    @Value("${openai.max-tokens:4000}")
    private Integer defaultMaxTokens;

    @Value("${openai.temperature:0.7}")
    private Double defaultTemperature;

    @Value("${openai.timeout:120}")
    private Integer timeoutSeconds;

    private final String vocabularySchema = """
{
  "type": "object",
  "properties": {
    "data": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "word": {
            "type": "string",
            "description": "The vocabulary word"
          },
          "ipa": {
            "type": "string",
            "description": "The IPA (International Phonetic Alphabet) pronunciation"
          },
          "type": {
            "type": "string",
            "description": "Part of speech (noun, verb, adjective, etc.)"
          },
          "level": {
            "type": "string",
            "description": "CEFR proficiency level",
            "enum": ["A1", "A2", "B1", "B2", "C1", "C2"]
          },
          "synonyms": {
            "type": "array",
            "items": {
              "type": "string"
            },
            "description": "Words with similar meanings"
          },
          "example": {
            "type": "string",
            "description": "Example sentence using the word"
          },
          "vietnameseTranslation": {
            "type": "string",
            "description": "Vietnamese translation of provided words"
          }
        },
        "required": ["word", "ipa", "type", "level", "synonyms", "example", "vietnameseTranslation"]
      }
    }
  },
  "required": ["data"]
}
""";

    @Autowired
    public VocabularyAIService(OpenAiService openAiService, JsonSchemaService jsonSchemaService) {
        this.openAiService = openAiService;
        this.jsonSchemaService = jsonSchemaService;
    }

    public VocabularyAIResponseDto generateVocabularyWords(String topic, String level, int wordCount, Set<String> excludedWords) {
        try {
            logger.info("Generating {} vocabulary words for topic '{}' at level {}, excluding {} words{}", wordCount,
                    topic, level, excludedWords.size(), String.join(", ", excludedWords));

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(vocabularySchema);

            // Create system instructions with exclusion list
            String systemInstruction = "You are an IELTS language learning assistant. " +
                    "Generate vocabulary words in JSON format. Each word should include: word, ipa, type, level, " +
                    "synonyms (array), and example (with the word highlighted using ** asterisks). " +
                    "Ensure the vocabulary words are appropriate for the requested CEFR level. " +
                    "DO NOT include any of these words: " + String.join(", ", excludedWords) + ". " +
                    "The output should be a valid JSON array of vocabulary word objects.\n\n" +
                    jsonSchemaService.createSchemaInstruction(schema);

            // Create the user prompt
            String prompt = String.format("Generate exactly %d vocabulary words for the topic \"%s\" at CEFR level %s." +
                            "Make sure to provide diverse and useful words that are not in the excluded list. If " +
                            "there is none in the current level, you can go up or down by 1 to 2 level." +
                            "The number of word is the highest priority, after that is the excluded list",
                    wordCount, topic, level);

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
            String content = result.getChoices().getFirst().getMessage().getContent();

            // Extract JSON content if wrapped in Markdown code blocks
            content = extractJsonContent(content);

            // Ensure valid JSON
            content = ensureValidJson(content);

            return objectMapper.readValue(content, VocabularyAIResponseDto.class);
        } catch (Exception e) {
            logger.error("Error generating vocabulary words", e);
            throw new RuntimeException("Failed to generate vocabulary words: " + e.getMessage(), e);
        }
    }

    public String generateParagraph(String topic, List<String> words) {
        try {
            logger.info("Generating paragraph for topic '{}' with {} words", topic, words.size());

            String wordList = String.join(", ", words);

            // Create system instructions
            String systemInstruction = "You are an IELTS language learning assistant. " +
                    "Create a natural-sounding paragraph about 1000 words that incorporates all the provided vocabulary words. " +
                    "Highlight each vocabulary word using ** (asterisks) when it appears in the paragraph. " +
                    "The paragraph should be appropriate for the topic and educational in nature.";

            // Create the user prompt
            String prompt = String.format("Generate a coherent paragraph about \"%s\" using the following vocabulary words: %s. " +
                            "Highlight each vocabulary word using ** (asterisks) when it appears in the paragraph.",
                    topic, wordList);

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
            return result.getChoices().getFirst().getMessage().getContent();
        } catch (Exception e) {
            logger.error("Error generating paragraph", e);
            throw new RuntimeException("Failed to generate paragraph: " + e.getMessage(), e);
        }
    }

    public FeedbackDto generateFeedback(String userWriting, String topic, String level, List<String> vocabularyWords) {
        try {
            logger.info("Generating feedback for writing on topic '{}' at level {}", topic, level);

            String wordList = String.join(", ", vocabularyWords);

            // Create the feedback schema
            String feedbackSchema = """
            {
              "type": "object",
              "properties": {
                "feedback": {
                  "type": "string",
                  "description": "Detailed feedback on the writing"
                },
                "improvedAnswer": {
                  "type": "string",
                  "description": "Improved version of the text"
                }
              },
              "required": ["feedback", "improvedAnswer"]
            }
            """;

            // Parse the schema
            JsonNode schema = jsonSchemaService.parseSchema(feedbackSchema);

            // Create system instructions
            String systemInstruction = "You are an IELTS language learning assistant. " +
                    "Provide constructive feedback on the learner's writing, as detailed as possible but not exceed " +
                    "1500 words in total response, " +
                    "focusing on" +
                    "vocabulary usage, grammar, " +
                    "and coherence. " +
                    "Then provide an improved version of the text, must use the original writing and improve, not cut" +
                    " off and must maintains the same ideas but " +
                    "uses the " +
                    "vocabulary words more effectively. " +
                    "Your response should be in JSON format with \"feedback\" and \"improvedAnswer\" fields.\n\n" +
                    jsonSchemaService.createSchemaInstruction(schema);

            // Create the user prompt
            String prompt = String.format("Provide feedback on this language learner's writing about \"%s\" at CEFR level %s. " +
                            "The writing should use these vocabulary words: %s. The words should be use, not " +
                            "compulsory, can use the word in another form\n\n" +
                            "Here is the learner's writing:\n\"%s\"\n\n" +
                            "Also provide an improved version of the text that maintains the same ideas but uses the vocabulary words more effectively.",
                    topic, level, wordList, userWriting);

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
            String content = result.getChoices().getFirst().getMessage().getContent();

            // Extract JSON content if wrapped in Markdown code blocks
            content = extractJsonContent(content);
            content = ensureValidJson(content);

            // Parse the response into the DTO

            return objectMapper.readValue(content, FeedbackDto.class);
        } catch (Exception e) {
            logger.error("Error generating feedback", e);
            throw new RuntimeException("Failed to generate feedback: " + e.getMessage(), e);
        }
    }

    private String extractJsonContent(String content) {
        // Check if content is wrapped in Markdown code blocks
        if (content.startsWith("```json") && content.endsWith("```")) {
            return content.substring(7, content.length() - 3).trim();
        } else if (content.startsWith("```") && content.endsWith("```")) {
            return content.substring(3, content.length() - 3).trim();
        }
        return content;
    }

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
            fixedJson.append("}".repeat(Math.max(0, openBraces - closeBraces)));
            fixedJson.append("]".repeat(Math.max(0, openBrackets - closeBrackets)));

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
