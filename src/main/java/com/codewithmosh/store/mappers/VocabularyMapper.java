package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.vocabulary.VocabularyDto;
import com.codewithmosh.store.dtos.vocabulary.VocabularyItemDto;
import com.codewithmosh.store.entities.Vocabulary;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {
    // Entity to DTO mapping
    @Mapping(source = "synonyms", target = "synonyms", qualifiedByName = "splitSynonyms")
    @Mapping(source = "session.id", target = "sessionId")
    VocabularyDto toDto(Vocabulary vocabulary);

    // DTO to Entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "synonyms", target = "synonyms", qualifiedByName = "joinSynonyms")
    @Mapping(target = "session", ignore = true)
    Vocabulary toEntity(VocabularyItemDto dto);

    // Helper method to split comma-separated synonyms string to List<String>
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