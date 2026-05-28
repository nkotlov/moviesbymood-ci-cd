package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "actors")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long actorId;

    @Column(nullable = false)
    private String actorFullName;

    private LocalDate actorBirthDate;

    @Column(columnDefinition = "TEXT")
    private String actorBiography;

    @OneToOne
    @JoinColumn(name = "photo_file_id")
    private FileInfo actorPhoto;

    @ManyToMany(mappedBy = "movieActors")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Movie> actorMovies = new HashSet<>();
}
