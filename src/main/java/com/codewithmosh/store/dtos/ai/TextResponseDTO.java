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
 * Simple text response DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextResponseDTO implements ResponseDTO {

    @JsonProperty("content")
    private String content;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private UsageDTO usage;

}
