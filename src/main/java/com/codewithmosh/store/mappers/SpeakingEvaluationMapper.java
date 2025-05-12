/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.SpeakingEvaluationDto;
import com.codewithmosh.store.entities.SpeakingEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SpeakingSessionMapper.class})
public interface SpeakingEvaluationMapper {
    /**
     * Map SpeakingEvaluation entity to SpeakingEvaluationDto
     * @param evaluation the entity to map
     * @return the mapped DTO
     */
    @Mapping(target = "speakingSession", source = "speakingSession")
    SpeakingEvaluationDto toDto(SpeakingEvaluation evaluation);

    /**
     * Map SpeakingEvaluationDto to SpeakingEvaluation entity
     * @param dto the DTO to map
     * @return the mapped entity
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SpeakingEvaluation toEntity(SpeakingEvaluationDto dto);
}