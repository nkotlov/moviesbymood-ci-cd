package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "genres")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long genreId;

    @Column(nullable = false, unique = true)
    private String genreName;

    @OneToOne
    @JoinColumn(name = "icon_file_id")
    private FileInfo genreIcon;

    @ManyToMany(mappedBy = "movieGenres")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Movie> genreMovies = new HashSet<>();
}
