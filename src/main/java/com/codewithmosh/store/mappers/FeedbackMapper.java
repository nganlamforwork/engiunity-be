package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.vocabulary.FeedbackDto;
import com.codewithmosh.store.entities.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackDto toDto(Feedback feedback);
}