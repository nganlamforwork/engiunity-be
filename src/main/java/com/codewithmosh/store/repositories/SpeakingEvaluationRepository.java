package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.SpeakingEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeakingEvaluationRepository extends JpaRepository<SpeakingEvaluation, Long> {
 Optional<SpeakingEvaluation> findBySpeakingSessionId(Long sessionId);
}