/**
 * Author: lamlevungan
 * Date: 07/05/2025
 **/
package com.codewithmosh.store.entities;

import com.codewithmosh.store.entities.enums.SpeakingPart;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpeakingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part", nullable = false)
    private SpeakingPart part;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "sub_questions", columnDefinition = "TEXT")
    private String subQuestions;

    @Column(name = "cue_card", columnDefinition = "TEXT")
    private String cueCard;

    @Column(name = "follow_up", columnDefinition = "TEXT")
    private String followUp;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaking_session_id", nullable = false)
    private SpeakingSession speakingSession;
}