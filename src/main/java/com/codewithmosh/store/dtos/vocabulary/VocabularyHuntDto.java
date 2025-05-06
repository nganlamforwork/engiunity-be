package com.codewithmosh.store.dtos.vocabulary;

import com.codewithmosh.store.dtos.ai.ResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VocabularyHuntDto implements ResponseDTO {
    @JsonProperty("data")
    private List<VocabularyItemDto> data;
}

