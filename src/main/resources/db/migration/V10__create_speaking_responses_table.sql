CREATE TABLE speaking_responses
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    audio_url   TEXT,
    transcript  TEXT,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME DEFAULT NULL,
    question_id BIGINT   NOT NULL,
    session_id  BIGINT   NOT NULL,

    CONSTRAINT fk_speaking_responses_question
        FOREIGN KEY (question_id) REFERENCES speaking_questions (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_speaking_responses_session
        FOREIGN KEY (session_id) REFERENCES speaking_sessions (id)
            ON DELETE CASCADE
);