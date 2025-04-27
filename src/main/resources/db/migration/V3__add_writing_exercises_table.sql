create table tags
(
    id    int auto_increment
        primary key,
    title varchar(100) not null
);

CREATE TABLE writing_exercises
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)                      NOT NULL,
    description     VARCHAR(255) NULL,
    thumbnail       VARCHAR(255) NULL COMMENT 'Thumbnail src url from cdn',
    creation_source VARCHAR(50) NOT NULL,
    part            VARCHAR(50)                       NOT NULL,
    exercise_type   VARCHAR(50) NULL,
    difficulty      VARCHAR(50) NULL,
    status          VARCHAR(50) DEFAULT 'NOT_STARTED' NOT NULL,
    score           FLOAT NULL,
    user_id         BIGINT NULL,
    CONSTRAINT `writing_exercises__users_id_fk`
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);
CREATE TABLE writing_exercise_detail
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    exercise_id BIGINT       NOT NULL,
    content     VARCHAR(255) NOT NULL,
    image       VARCHAR(255) NULL,
    CONSTRAINT `writing_exercise_detail__writing_exercises_id_fk`
        FOREIGN KEY (exercise_id) REFERENCES writing_exercises (id)
            ON DELETE CASCADE
);
CREATE TABLE exercises_tags
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    exercise_id   BIGINT      NOT NULL,
    exercise_type VARCHAR(50) NOT NULL,
    tag_id        INT         NOT NULL,
    CONSTRAINT exercises_tags_pk_2 UNIQUE (exercise_id, exercise_type, tag_id),
    CONSTRAINT exercises_tags__writing_exercises_id_fk FOREIGN KEY (exercise_id)
        REFERENCES writing_exercises (id)
        ON DELETE CASCADE,
    CONSTRAINT exercises_tags__tags_id_fk FOREIGN KEY (tag_id)
        REFERENCES tags (id)
        ON DELETE CASCADE
);
