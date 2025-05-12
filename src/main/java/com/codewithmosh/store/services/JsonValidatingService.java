/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class JsonValidatingService {
    private static final Logger logger = LoggerFactory.getLogger(JsonValidatingService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract JSON content from a string that might be wrapped in markdown code blocks
     */
    public String extractJsonContent(String content) {
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
    public String ensureValidJson(String jsonContent) {
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
