package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.SpeakingQuestionDto;
import com.codewithmosh.store.entities.SpeakingQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
@Mapper(componentModel = "spring")
public interface SpeakingQuestionMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "part", source = "part")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "text", source = "text")
    default SpeakingQuestionDto toDto(SpeakingQuestion question) {
        if (question == null) return null;

        SpeakingQuestionDto dto = new SpeakingQuestionDto();
        dto.setId(question.getId());
        dto.setOrder(question.getOrder());
        dto.setText(question.getText());
        dto.setPart(question.getPart());

        dto.setSubQuestionsFromString(question.getSubQuestions());
        dto.setCueCardFromString(question.getCueCard());
        dto.setFollowUpFromString(question.getFollowUp());

        return dto;
    }

    @Mapping(target = "subQuestions", expression = "java(questionDto.getSubQuestionsAsString())")
    @Mapping(target = "cueCard", expression = "java(questionDto.getCueCardAsString())")
    @Mapping(target = "followUp", expression = "java(questionDto.getFollowUpAsString())")
    @Mapping(target = "id", ignore = true)
    SpeakingQuestion toEntity(SpeakingQuestionDto questionDto);

    // Static helper
    static List<String> stringToList(String input) {
        if (input == null || input.isBlank()) return null;
        return Arrays.stream(input.split("\\R")) // Split on any line separator
                .map(String::trim)
                .collect(Collectors.toList());
    }
}