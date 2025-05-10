/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.SpeakingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeakingResponseRepository extends JpaRepository<SpeakingResponse, Long> {

 List<SpeakingResponse> findBySessionId(Long sessionId);

 @Query("SELECT r FROM SpeakingResponse r WHERE r.session.id = :sessionId AND r.question.id = :questionId")
 Optional<SpeakingResponse> findBySessionIdAndQuestionId(
         @Param("sessionId") Long sessionId,
         @Param("questionId") Long questionId);
}