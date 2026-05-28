package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "playlists")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Playlist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long playlistId;

    @Column(nullable = false)
    private String playlistName;

    @Column(nullable = false)
    private Instant playlistCreatedAt;

    @PrePersist
    public void onCreate() {
        this.playlistCreatedAt = Instant.now();
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User playlistUser;

    @OneToOne
    @JoinColumn(name = "poster_file_id")
    private FileInfo playlistPoster;

    @ManyToMany
    @JoinTable(name = "playlist_movies",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Movie> playlistMovies = new HashSet<>();
}
