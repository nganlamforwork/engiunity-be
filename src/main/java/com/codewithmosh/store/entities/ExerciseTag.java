package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "exercises_tags", schema = "enginuity")
public class ExerciseTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exercise_type")
    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

}