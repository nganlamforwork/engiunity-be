/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.dtos.ai;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for structured responses with dynamic fields
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StructuredResponseDTO implements ResponseDTO {

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private UsageDTO usage;

    private Map<String, Object> properties = new HashMap<>();


    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
