package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.SpeakingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: lamlevungan
 * Date: 08/05/2025
 **/
public interface SpeakingSessionRepository extends JpaRepository<SpeakingSession, Long> {
    List<SpeakingSession> findByUserId(Long userId);
}
