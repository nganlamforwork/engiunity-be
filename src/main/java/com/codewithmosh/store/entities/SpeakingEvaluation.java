/**
 * Author: lamlevungan
 * Date: 09/05/2025
 **/
package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.ScoreStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "speaking_evaluations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpeakingEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "score", nullable = true)
    private Double score;

    @Builder.Default
    @Column(name = "score_status")
    @Enumerated(EnumType.STRING)
    private ScoreStatus scoreStatus = ScoreStatus.NOT_SCORED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "score_detail", columnDefinition = "jsonb")
    private Map<String, Object> scoreDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaking_session_id", nullable = false)
    private SpeakingSession speakingSession;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Set default status if not set
        if (scoreStatus == null) {
            scoreStatus = ScoreStatus.NOT_SCORED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
