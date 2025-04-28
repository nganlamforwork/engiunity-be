package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.ScoreStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
public class WritingExerciseResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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

    @ColumnDefault("1")
    @Column(name = "take_number")
    private Integer takeNumber;

    @ColumnDefault("0")
    @Column(name = "score")
    private Integer score;

    @Column(name = "score_detail")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> scoreDetail;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_SCORED'")
    @Column(name = "score_status", length = 50)
    private ScoreStatus scoreStatus;

}
