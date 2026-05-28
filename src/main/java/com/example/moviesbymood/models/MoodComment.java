package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "mood_comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MoodComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long moodCommentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private MoodCategory commentMood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User commentUser;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String moodCommentText;

    @Column(nullable = false)
    private Instant moodCommentCreatedAt;
}
