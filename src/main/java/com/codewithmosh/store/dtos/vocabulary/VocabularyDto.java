package com.codewithmosh.store.dtos.vocabulary;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VocabularyDto {
    private Long id;
    private String word;
    private String ipa;
    private String type;
    private String level;
    private List<String> synonyms;
    private String example;
    private String vietnameseTranslation;
    private Long sessionId;
}