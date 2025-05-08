/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import com.codewithmosh.store.entities.enums.SpeakingSessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "speaking_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SpeakingSessionStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false)
    private SpeakingPart part;

    @OneToMany(mappedBy = "speakingSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpeakingQuestion> questions = new ArrayList<>();

    /**
     * Add a question to this session
     * @param question the question to add
     */
    public void addQuestion(SpeakingQuestion question) {
        questions.add(question);
        question.setSpeakingSession(this);
    }

    /**
     * Remove a question from this session
     * @param question the question to remove
     */
    public void removeQuestion(SpeakingQuestion question) {
        questions.remove(question);
        question.setSpeakingSession(null);
    }
}