package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.vocabulary.ParagraphDto;
import com.codewithmosh.store.entities.Paragraph;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ParagraphMapper {
    @Mapping(source = "content", target = "paragraph")
    ParagraphDto toDto(Paragraph paragraph);
}
