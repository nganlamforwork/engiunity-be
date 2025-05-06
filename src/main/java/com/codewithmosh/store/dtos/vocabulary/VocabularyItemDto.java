package com.codewithmosh.store.dtos.vocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VocabularyItemDto {
    @JsonProperty("word")
    private String word;

    @JsonProperty("ipa")
    private String ipa;

    @JsonProperty("type")
    private String type;

    @JsonProperty("level")
    private String level;

    @JsonProperty("synonyms")
    private List<String> synonyms;

    @JsonProperty("example")
    private String example;

    @JsonProperty("vietnamese_translation")
    private String vietnameseTranslation;
}