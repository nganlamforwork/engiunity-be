/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for conversation responses
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponseDTO implements ResponseDTO {

    @JsonProperty("content")
    private String content;

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<Map<String, String>> messages;

    @JsonProperty("usage")
    private UsageDTO usage;

}
