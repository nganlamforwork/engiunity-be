package com.codewithmosh.store.dtos.vocabulary;

import lombok.Data;
import java.util.List;

@Data
public class SessionDetailDto {
    private String topic;
    private String level;
    private List<VocabularyDto> vocabularies;
    private String paragraph;
    private String writing;
    private String feedback;
    private String improvedAnswer;
}