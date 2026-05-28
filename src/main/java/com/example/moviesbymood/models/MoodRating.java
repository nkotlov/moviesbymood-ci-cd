package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mood_ratings",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_mood_ratings_mood_user",
                columnNames = {"mood_id","user_id"}))
public class MoodRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long moodRatingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    private MoodCategory ratedMood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User ratingUser;

    @Column(nullable = false)
    private Short moodRatingScore;

    @Column(nullable = false)
    private Instant moodRatingCreatedAt;
}
