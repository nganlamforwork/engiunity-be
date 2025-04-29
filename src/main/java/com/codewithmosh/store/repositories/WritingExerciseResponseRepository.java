/**
 * Author: lamlevungan
 * Date: 28/04/2025
 **/
package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.entities.WritingExerciseResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WritingExerciseResponseRepository  extends JpaRepository<WritingExerciseResponse, Integer> {
    @Query("SELECT MAX(r.takeNumber) FROM WritingExerciseResponse r WHERE r.exercise.id = :exerciseId")
    Integer findMaxTakeNumberByExerciseId(@Param("exerciseId") Long exerciseId);

    @Query("SELECT r FROM WritingExerciseResponse r WHERE r.exercise.id = :exerciseId AND r.scoreStatus = 'NOT_SCORED' ORDER BY r.takenAt DESC")
    Optional<WritingExerciseResponse> findLatestNotGradedResponseByExerciseId(@Param("exerciseId") Long exerciseId);

    @Modifying
    @Transactional
    @Query("UPDATE WritingExerciseResponse r SET r.score = :score, r.scoreDetail = :scoreDetail, r.scoreStatus = :scoreStatus WHERE r.id = :responseId")
    int updateScoreFieldsById(
            @Param("responseId") Long responseId,
            @Param("score") Double score,
            @Param("scoreDetail") String scoreDetail,
            @Param("scoreStatus") String scoreStatus
    );

    Optional<WritingExerciseResponse> findById(Long id);
}
