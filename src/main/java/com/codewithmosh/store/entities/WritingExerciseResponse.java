package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.ScoreStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "writing_exercise_responses", schema = "enginuity")
@ToString()
public class WritingExerciseResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "exercise_id")
    private WritingExercise exercise;

    @Lob
    @Column(name = "content")
    private String content;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "taken_at")
    private Instant takenAt;

    @Builder.Default
    @Column(name = "take_number")
    private Integer takeNumber = 1;

    @Builder.Default
    @Column(name = "score", nullable = false)
    private Float score = 0.0F;

    @Column(name = "score_detail")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> scoreDetail;

    @Builder.Default
    @Column(name = "score_status")
    @Enumerated(EnumType.STRING)
    private ScoreStatus scoreStatus = ScoreStatus.NOT_SCORED;
}