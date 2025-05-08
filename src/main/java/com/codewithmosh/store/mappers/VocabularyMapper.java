package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.vocabulary.GetAllVocabularyDto;
import com.codewithmosh.store.dtos.vocabulary.VocabularyDto;
import com.codewithmosh.store.entities.Vocabulary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {
    @Mapping(source = "synonyms", target = "synonyms", qualifiedByName = "splitSynonyms")
    VocabularyDto toDto(Vocabulary vocabulary);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "synonyms", target = "synonyms", qualifiedByName = "joinSynonyms")
    @Mapping(target = "session", ignore = true)
    Vocabulary toEntity(VocabularyDto dto);

    List<VocabularyDto> toDtoList(List<Vocabulary> vocabularies);

    default GetAllVocabularyDto toListDto(List<Vocabulary> vocabularies) {
        GetAllVocabularyDto dto = new GetAllVocabularyDto();
        dto.setVocabularies(toDtoList(vocabularies));
        return dto;
    }

    @Named("splitSynonyms")
    default List<String> splitSynonyms(String synonyms) {
        if (synonyms == null || synonyms.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(synonyms.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    // Helper method to join List<String> synonyms to comma-separated string
    @Named("joinSynonyms")
    default String joinSynonyms(List<String> synonyms) {
        if (synonyms == null || synonyms.isEmpty()) {
            return "";
        }
        return String.join(", ", synonyms);
    }
}