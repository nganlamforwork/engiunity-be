/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for token usage information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageDTO {

    @JsonProperty("prompt_tokens")
    private Long promptTokens;

    @JsonProperty("completion_tokens")
    private Long completionTokens;

    @JsonProperty("total_tokens")
    private Long totalTokens;
}
