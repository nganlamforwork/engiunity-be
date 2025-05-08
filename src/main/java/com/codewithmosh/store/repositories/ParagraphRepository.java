package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    Optional<Paragraph> findBySessionId(Long sessionId);
}