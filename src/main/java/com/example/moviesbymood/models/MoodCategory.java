package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "mood_categories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MoodCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long moodId;

    @Column(nullable = false, unique = true)
    private String moodName;

    @Column(columnDefinition = "TEXT")
    private String moodDescription;

    @OneToOne
    @JoinColumn(name = "icon_file_id")
    private FileInfo moodIcon;

    @ManyToMany(mappedBy = "movieMoods")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Movie> moodMovies = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteMoods")
    private Set<User> moodFans = new HashSet<>();
}
