/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.SpeakingResponseDto;
import com.codewithmosh.store.dtos.speaking.SpeakingResponseRequestDto;
import com.codewithmosh.store.entities.SpeakingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpeakingResponseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SpeakingResponse toEntity(SpeakingResponseDto response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SpeakingResponse toEntity(SpeakingResponseRequestDto request);

    @Mapping(target = "questionId", source = "question.id")
    SpeakingResponseDto toDto(SpeakingResponse entity);
}