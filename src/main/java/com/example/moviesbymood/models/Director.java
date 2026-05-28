package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "directors")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long directorId;

    @Column(nullable = false)
    private String directorFullName;

    private LocalDate directorBirthDate;

    @Column(columnDefinition = "TEXT")
    private String directorBiography;

    @OneToOne
    @JoinColumn(name = "photo_file_id")
    private FileInfo directorPhoto;

    @ManyToMany(mappedBy = "movieDirectors")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Movie> directorMovies = new HashSet<>();
}
