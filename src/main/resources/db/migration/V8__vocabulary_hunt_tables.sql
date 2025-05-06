-- Create session table
CREATE TABLE session (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         status VARCHAR(50) NOT NULL,
                         user_id BIGINT NOT NULL,
                         topic VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create vocabulary table with updated schema
CREATE TABLE vocabulary (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            word VARCHAR(255) NOT NULL,
                            ipa VARCHAR(255) NOT NULL,
                            word_type VARCHAR(50) NOT NULL,
                            level VARCHAR(2) NOT NULL,
                            example TEXT NOT NULL,
                            synonyms TEXT,
                            vietnamese_translation VARCHAR(255),
                            session_id BIGINT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (session_id) REFERENCES session(id)
);

CREATE TABLE paragraph (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           content TEXT NOT NULL,
                           session_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (session_id) REFERENCES session(id)
);

-- Create feedback table
CREATE TABLE feedback (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          feedback TEXT NOT NULL,
                          improved_answer TEXT NOT NULL,
                          user_writing TEXT NOT NULL,
                          session_id BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (session_id) REFERENCES session(id)
);

-- Create index for faster lookups
CREATE INDEX idx_paragraph_session ON paragraph(session_id);
CREATE INDEX idx_vocabulary_level ON vocabulary(level);
CREATE INDEX idx_vocabulary_word_type ON vocabulary(word_type);
CREATE INDEX idx_vocabulary_session ON vocabulary(session_id);
CREATE INDEX idx_session_status ON session(status);
CREATE INDEX idx_session_topic ON session(topic);
CREATE INDEX idx_session_user_id ON session(user_id);
CREATE INDEX idx_feedback_session ON feedback(session_id);