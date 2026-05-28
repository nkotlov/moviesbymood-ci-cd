package com.example.moviesbymood.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long userId;

    @Column(nullable = false, unique = true)
    private String userNickname;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String userRole;

    @Column(nullable = false)
    private LocalDate userRegistrationDate;

    private String confirmCode;

    @Column(nullable = false)
    private boolean confirmed;

    @Column(nullable = false)
    private boolean oauthOnly;

    private String passwordResetToken;

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    private FileInfo avatar;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @ManyToMany
    @JoinTable(name = "user_favorite_movies",
            joinColumns        = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> favoriteMovies = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_favorite_moods",
            joinColumns        = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "mood_id"))
    private Set<MoodCategory> favoriteMoods = new HashSet<>();

    @OneToMany(mappedBy = "fileInfoUploadedBy", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<FileInfo> uploads = new ArrayList<>();

    @OneToMany(mappedBy = "playlistUser", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "ratingUser", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "commentUser", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "ratingUser", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<MoodRating> moodRatings = new ArrayList<>();

    @OneToMany(mappedBy = "commentUser", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<MoodComment> moodComments = new ArrayList<>();

    public void addFavoriteMovie(Movie movie) {
        if (movie == null) return;
        favoriteMovies.add(movie);
        if (movie.getMovieFans() != null) {
            movie.getMovieFans().add(this);
        }
    }

    public void removeFavoriteMovie(Movie movie) {
        if (movie == null) return;
        favoriteMovies.remove(movie);
        if (movie.getMovieFans() != null) {
            movie.getMovieFans().remove(this);
        }
    }

    public void addFavoriteMood(MoodCategory mood) {
        if (mood == null) return;
        favoriteMoods.add(mood);
        if (mood.getMoodFans() != null) {
            mood.getMoodFans().add(this);
        }
    }

    public void removeFavoriteMood(MoodCategory mood) {
        if (mood == null) return;
        favoriteMoods.remove(mood);
        if (mood.getMoodFans() != null) {
            mood.getMoodFans().remove(this);
        }
    }
}
