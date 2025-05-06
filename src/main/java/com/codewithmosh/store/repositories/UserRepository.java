package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User findById(long id);

    @Query("SELECT s.userId FROM Session s WHERE s.id = :sessionId")
    Long findBySessionId(Long sessionId);
}
