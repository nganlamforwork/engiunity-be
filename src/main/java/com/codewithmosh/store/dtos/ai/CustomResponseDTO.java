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
 * Example of a custom response DTO that can be extended by users
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseDTO implements ResponseDTO {

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("confidence")
    private Double confidence;

    @JsonProperty("sources")
    private String[] sources;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private UsageDTO usage;

}
