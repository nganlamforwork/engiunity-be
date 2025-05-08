CREATE TABLE speaking_sessions
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT                      NOT NULL,
    status     VARCHAR(255) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    topic      VARCHAR(255)                NOT NULL,
    notes      TEXT NULL,
    part       VARCHAR(255) DEFAULT 'FULL' NOT NULL,
    CONSTRAINT speaking_sessions_users_id_fk
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
) AUTO_INCREMENT = 1;

CREATE TABLE questions
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    part                INT    NOT NULL,
    text                TEXT   NOT NULL,
    sub_questions       TEXT NULL,
    cue_card            TEXT NULL,
    follow_up           TEXT NULL,
    `order`             INT    NOT NULL,
    speaking_session_id BIGINT NOT NULL,
    CONSTRAINT questions_speaking_session_id_fk
        FOREIGN KEY (speaking_session_id)
            REFERENCES speaking_sessions (id)
            ON DELETE CASCADE
) AUTO_INCREMENT = 1;