package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.WritingExercise;
import com.codewithmosh.store.entities.enums.CreationSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 **/
public interface WritingExerciseRepository extends JpaRepository<WritingExercise, Long> {

    // Query to get all exercises by a specific user (user_created) or by ai_generated and system_uploaded
    @Query("SELECT we FROM WritingExercise we " +
            "WHERE (we.user.id = :userId AND we.creationSource = :userCreated) " +
            "OR we.creationSource IN (:aiGenerated, :systemUploaded)")
    List<WritingExercise> findAllByUserIdAndCreationSource(
            Long userId,
            CreationSource userCreated,
            CreationSource aiGenerated,
            CreationSource systemUploaded);

    Optional<WritingExercise> findById(Long id);
}
