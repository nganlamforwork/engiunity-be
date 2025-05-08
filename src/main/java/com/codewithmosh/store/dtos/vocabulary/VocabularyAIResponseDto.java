package com.codewithmosh.store.dtos.vocabulary;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.codewithmosh.store.dtos.ai.TokenUsageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VocabularyAIResponseDto implements ResponseDTO {
    @JsonProperty("data")
    private List<VocabularyDto> data;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private TokenUsageDTO usage;
}

