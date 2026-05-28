package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "movies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long movieId;

    @Column(nullable = false)
    private String movieTitle;

    @Column(columnDefinition = "TEXT")
    private String movieDescription;

    private LocalDate movieReleaseDate;

    @Column(nullable = false)
    private Instant movieCreatedAt;

    @Column(nullable = false)
    private Integer movieDuration;

    @PrePersist
    public void prePersist() {
        this.movieCreatedAt = Instant.now();
    }

    @OneToOne
    @JoinColumn(name = "poster_file_id")
    private FileInfo moviePoster;

    @ManyToMany
    @JoinTable(name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Genre> movieGenres = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Actor> movieActors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "movie_directors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Director> movieDirectors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "movie_moods",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "mood_id"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<MoodCategory> movieMoods = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteMovies")
    private Set<User> movieFans = new HashSet<>();
}
