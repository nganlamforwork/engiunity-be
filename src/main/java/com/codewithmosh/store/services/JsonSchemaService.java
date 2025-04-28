/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.services;


import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsonSchemaService {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse a JSON schema string into a JsonNode
     */
    public JsonNode parseSchema(String schema) {
        try {
            return objectMapper.readTree(schema);
        } catch (IOException e) {
            logger.error("Failed to parse JSON schema", e);
            throw new RuntimeException("Invalid JSON schema: " + e.getMessage(), e);
        }
    }

    /**
     * Add token usage fields to a JSON schema
     */
    public JsonNode addTokenUsageToSchema(JsonNode schema) {
        try {
            // Clone the schema to avoid modifying the original
            ObjectNode schemaWithUsage = schema.deepCopy();

            // Get the properties node
            ObjectNode properties = (ObjectNode) schemaWithUsage.get("properties");
            if (properties == null) {
                properties = objectMapper.createObjectNode();
                ((ObjectNode) schemaWithUsage).set("properties", properties);
            }

            // Add token usage properties
            ObjectNode usageSchema = objectMapper.createObjectNode();
            usageSchema.put("type", "object");

            ObjectNode usageProperties = objectMapper.createObjectNode();

            ObjectNode inputTokens = objectMapper.createObjectNode();
            inputTokens.put("type", "integer");
            inputTokens.put("description", "Number of tokens in the input/prompt");

            ObjectNode outputTokens = objectMapper.createObjectNode();
            outputTokens.put("type", "integer");
            outputTokens.put("description", "Number of tokens in the output/completion");

            ObjectNode totalTokens = objectMapper.createObjectNode();
            totalTokens.put("type", "integer");
            totalTokens.put("description", "Total number of tokens used");

            usageProperties.set("input_tokens", inputTokens);
            usageProperties.set("output_tokens", outputTokens);
            usageProperties.set("total_tokens", totalTokens);

            usageSchema.set("properties", usageProperties);

            // Add required fields for usage
            ArrayNode requiredUsage = objectMapper.createArrayNode()
                    .add("input_tokens")
                    .add("output_tokens")
                    .add("total_tokens");
            usageSchema.set("required", requiredUsage);

            // Add the usage schema to the properties
            properties.set("usage", usageSchema);

            // Add model field if it doesn't exist
            if (!properties.has("model")) {
                ObjectNode modelSchema = objectMapper.createObjectNode();
                modelSchema.put("type", "string");
                modelSchema.put("description", "The model used for generation");
                properties.set("model", modelSchema);
            }

            return schemaWithUsage;
        } catch (Exception e) {
            logger.error("Failed to add token usage to schema", e);
            throw new RuntimeException("Failed to add token usage to schema: " + e.getMessage(), e);
        }
    }

    /**
     * Convert a JSON schema to a system instruction for the AI model
     */
    public String createSchemaInstruction(JsonNode schema) {
        // Add token usage fields to the schema
        JsonNode schemaWithUsage = addTokenUsageToSchema(schema);

        StringBuilder instruction = new StringBuilder();
        instruction.append("You must respond with a JSON object that strictly follows this schema:\n\n");
        instruction.append(schemaWithUsage.toString());
        instruction.append("\n\nIMPORTANT INSTRUCTIONS:");
        instruction.append("\n1. Do not include any explanations or text outside of the JSON object.");
        instruction.append("\n2. The response must be valid JSON that can be parsed directly.");
        instruction.append("\n3. Keep text fields concise and to the point.");
        instruction.append("\n4. For array items, limit to 3 items maximum per category to ensure the response fits within token limits.");
        instruction.append("\n5. Ensure all required fields are included.");
        instruction.append("\n6. For the 'usage' field, leave it empty with placeholder values. It will be filled in by the system.");

        return instruction.toString();
    }

    /**
     * Create a function calling definition from a JSON schema
     */
    public String createFunctionDefinition(String name, String description, JsonNode schema) {
        try {
            // Add token usage fields to the schema
            JsonNode schemaWithUsage = addTokenUsageToSchema(schema);

            StringBuilder functionDef = new StringBuilder();
            functionDef.append("{\n");
            functionDef.append("  \"name\": \"").append(name).append("\",\n");
            functionDef.append("  \"description\": \"").append(description).append("\",\n");
            functionDef.append("  \"parameters\": ").append(schemaWithUsage.toString()).append("\n");
            functionDef.append("}");

            return functionDef.toString();
        } catch (Exception e) {
            logger.error("Failed to create function definition", e);
            throw new RuntimeException("Failed to create function definition: " + e.getMessage(), e);
        }
    }

    /**
     * Update a JSON response with token usage information
     */
    public String addTokenUsageToResponse(String jsonResponse, TokenUsageDTO usage) {
        try {
            // Parse the JSON response
            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            // Create a mutable copy
            ObjectNode mutableResponse = responseNode.deepCopy();

            // Create the usage node
            ObjectNode usageNode = objectMapper.createObjectNode();
            usageNode.put("input_tokens", usage.getInputTokens());
            usageNode.put("output_tokens", usage.getOutputTokens());
            usageNode.put("total_tokens", usage.getTotalTokens());

            // Add the usage node to the response
            mutableResponse.set("usage", usageNode);

            // Return the updated JSON
            return objectMapper.writeValueAsString(mutableResponse);
        } catch (Exception e) {
            logger.error("Failed to add token usage to response", e);
            return jsonResponse; // Return the original response if there's an error
        }
    }
}
