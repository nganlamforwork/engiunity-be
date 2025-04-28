CREATE TABLE writing_exercise_responses
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    exercise_id  BIGINT NOT NULL,
    content      TEXT NOT NULL,
    taken_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    take_number  INT DEFAULT 1,
    score        INT DEFAULT 0 NOT NULL,
    score_detail JSON NULL,
    score_status VARCHAR(50) DEFAULT 'NOT_SCORED' NOT NULL,
    CONSTRAINT writing_exercise_responses_writing_exercises_id_fk
        FOREIGN KEY (exercise_id) REFERENCES writing_exercises (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);
