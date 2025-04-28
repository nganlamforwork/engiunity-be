/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.ai;
import java.util.HashMap;
import java.util.Map;

/**
 * A template for creating structured prompts with variable substitution
 */
public class PromptTemplate {
    private final String template;

    public PromptTemplate(String template) {
        this.template = template;
    }

    /**
     * Format the template by replacing variables with their values
     *
     * @param variables Map of variable names to values
     * @return The formatted prompt
     */
    public String format(Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * Create a builder for the template
     *
     * @return A new builder
     */
    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * Builder for creating formatted prompts
     */
    public static class Builder {
        private final PromptTemplate template;
        private final Map<String, String> variables = new HashMap<>();

        private Builder(PromptTemplate template) {
            this.template = template;
        }

        /**
         * Add a variable value
         *
         * @param name Variable name
         * @param value Variable value
         * @return This builder
         */
        public Builder with(String name, String value) {
            variables.put(name, value);
            return this;
        }

        /**
         * Build the formatted prompt
         *
         * @return The formatted prompt
         */
        public String build() {
            return template.format(variables);
        }
    }
}
