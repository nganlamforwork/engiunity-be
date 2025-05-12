package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.CreateSpeakingSessionRequest;
import com.codewithmosh.store.dtos.speaking.SpeakingSessionDto;
import com.codewithmosh.store.dtos.speaking.UpdateSpeakingSessionRequest;
import com.codewithmosh.store.entities.SpeakingSession;
import org.mapstruct.*;
/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/

@Mapper(componentModel = "spring", uses = {SpeakingQuestionMapper.class})
public interface SpeakingSessionMapper {

    /**
     * Convert CreateSpeakingSessionRequest to SpeakingSession entity
     * @param request the request DTO
     * @param userId the user ID
     * @return SpeakingSession entity
     */
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SpeakingSession toEntity(CreateSpeakingSessionRequest request, Long userId);

    /**
     * Convert SpeakingSession entity to SpeakingSessionResponse DTO
     * @param session the entity
     * @return SpeakingSession DTO
     */
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SpeakingSessionDto toDto(SpeakingSession session);
}