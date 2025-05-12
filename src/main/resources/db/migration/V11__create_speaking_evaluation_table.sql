CREATE TABLE speaking_evaluations
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    score               DOUBLE,
    score_status        VARCHAR(50)       DEFAULT 'NOT_SCORED',
    score_detail        JSON,
    speaking_session_id BIGINT   NOT NULL UNIQUE,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_speaking_evaluation_session
        FOREIGN KEY (speaking_session_id)
            REFERENCES speaking_sessions (id)
            ON DELETE CASCADE
);