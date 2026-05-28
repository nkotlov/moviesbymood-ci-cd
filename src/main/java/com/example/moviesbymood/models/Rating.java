package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ratings_movie_user",
                columnNames = {"movie_id","user_id"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rating {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long ratingId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie ratedMovie;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User ratingUser;

    @Column(nullable = false)
    private Short ratingScore;

    @Column(nullable = false)
    private Instant ratingCreatedAt;

    @PrePersist
    public void onRate() {
        this.ratingCreatedAt = Instant.now();
    }
}
