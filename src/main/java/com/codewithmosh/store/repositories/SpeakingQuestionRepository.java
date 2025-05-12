package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.SpeakingQuestion;
import com.codewithmosh.store.entities.SpeakingSession;
import com.codewithmosh.store.entities.enums.SpeakingPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
public interface SpeakingQuestionRepository extends JpaRepository<SpeakingQuestion, Long> {

    List<SpeakingQuestion> findBySpeakingSession(SpeakingSession session);
    List<SpeakingQuestion> findBySpeakingSessionId(Long sessionId);

    @Query("""
    SELECT q FROM SpeakingQuestion q
    WHERE q.speakingSession.id = :sessionId
    AND (:part IS NULL OR q.part = :part)
    AND (:questionId IS NULL OR q.id = :questionId)
    AND (:order IS NULL OR q.order = :order)
    ORDER BY q.part, q.order
""")
    List<SpeakingQuestion> findByFilters(
            @Param("sessionId") Long sessionId,
            @Param("part") SpeakingPart part,
            @Param("questionId") Long questionId,
            @Param("order") Integer order
    );
}
