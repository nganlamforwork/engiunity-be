package com.codewithmosh.store.dtos.vocabulary;

import com.codewithmosh.store.entities.enums.SessionStatus;
import lombok.Data;

@Data
public class SessionDto {
    private Long id;
    private Long userId;
    private String topic;
    private String level;
    private SessionStatus status;
}

