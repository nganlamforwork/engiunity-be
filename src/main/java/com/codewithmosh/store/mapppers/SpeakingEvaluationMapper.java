/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.speaking.evaluation.SpeakingEvaluationDto;
import com.codewithmosh.store.entities.SpeakingEvaluation;
import com.codewithmosh.store.entities.SpeakingSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface SpeakingEvaluationMapper {

}