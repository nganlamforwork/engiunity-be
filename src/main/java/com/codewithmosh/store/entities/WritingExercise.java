package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "writing_exercises")
public class WritingExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "creation_source")
    @Enumerated(EnumType.STRING)
    private String creationSource;

    @Column(name = "part")
    @Enumerated(EnumType.STRING)
    private String part;

    @Column(name = "exercise_type")
    @Enumerated(EnumType.STRING)
    private String exerciseType;

    @Column(name = "difficulty")
    @Enumerated(EnumType.STRING)
    private String difficulty;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private String status;

    @Column(name = "score")
    private Float score;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

}