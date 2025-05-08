package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.AIQuestionDto;
import com.codewithmosh.store.dtos.speaking.SpeakingQuestionDto;
import com.codewithmosh.store.dtos.speaking.SpeakingSessionDto;
import com.codewithmosh.store.entities.SpeakingQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
@Mapper(componentModel = "spring")
public interface SpeakingQuestionMapper {

 /**
  * Convert Question entity to QuestionResponse DTO
  * @param question the entity
  * @return QuestionResponse DTO
  */
 SpeakingQuestionDto toDto(SpeakingQuestion question);

 /**
  * Convert AIQuestion to Question entity
  * @param aiQuestion the AI generated question
  * @param session the speaking session
  * @return Question entity
  */
 @Mapping(target = "subQuestions", expression = "java(aiQuestion.getSubQuestionsAsString())")
 @Mapping(target = "cueCard", expression = "java(aiQuestion.getCueCardAsString())")
 @Mapping(target = "followUp", expression = "java(aiQuestion.getFollowUpAsString())")
 @Mapping(target = "id", ignore = true)
 SpeakingQuestion toEntity(AIQuestionDto aiQuestion);
}
