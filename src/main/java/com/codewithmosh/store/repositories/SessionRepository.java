package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
}