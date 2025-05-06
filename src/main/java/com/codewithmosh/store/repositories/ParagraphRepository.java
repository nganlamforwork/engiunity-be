package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
}