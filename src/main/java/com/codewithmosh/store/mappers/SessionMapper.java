package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.vocabulary.GetSessionsDto;
import com.codewithmosh.store.dtos.vocabulary.SessionDto;
import com.codewithmosh.store.entities.Session;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionDto toDto(Session session);

    List<SessionDto> toDtoList(List<Session> sessions);

    default GetSessionsDto toListDto(List<Session> sessions) {
        GetSessionsDto dto = new GetSessionsDto();
        dto.setSessions(toDtoList(sessions));
        return dto;
    }
}
