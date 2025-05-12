package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findAllBySessionId(Long sessionId);

    @Query("SELECT v.word FROM Vocabulary v JOIN v.session s WHERE s.userId = :userId")
    Set<String> findAllWordsByUserId(@Param("userId") Long userId);

    @Query("SELECT v FROM Vocabulary v WHERE v.session.userId = :userId")
    List<Vocabulary> findAllByUserId(Long userId);
}