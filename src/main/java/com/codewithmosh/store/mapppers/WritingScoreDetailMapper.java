package com.codewithmosh.store.mapppers;

import com.codewithmosh.store.dtos.score.WritingScoreDetailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
@Getter
public class WritingScoreDetailMapper {

    private final ObjectMapper objectMapper;

    public WritingScoreDetailDto fromJson(String json) throws IOException {
        return objectMapper.readValue(json, WritingScoreDetailDto.class);
    }

    public String toJson(WritingScoreDetailDto dto) throws IOException {
        return objectMapper.writeValueAsString(dto);
    }
}
