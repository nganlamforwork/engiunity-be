package com.codewithmosh.store.dtos.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllVocabularyDto {
    private List<VocabularyDto> vocabularies;
}
