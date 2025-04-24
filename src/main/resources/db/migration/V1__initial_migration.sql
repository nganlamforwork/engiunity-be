CREATE TABLE module
(
    id   TINYINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE profiles
(
    id             BIGINT NOT NULL,
    bio            LONGTEXT NULL,
    phone_number   VARCHAR(15) NULL,
    date_of_birth  date NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

ALTER TABLE profiles
    ADD CONSTRAINT profiles_ibfk_1 FOREIGN KEY (id) REFERENCES users (id) ON DELETE NO ACTION;