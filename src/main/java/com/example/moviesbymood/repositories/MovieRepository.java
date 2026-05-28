package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Movie;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        SELECT DISTINCT m
        FROM Movie m
            LEFT JOIN m.movieMoods md
            LEFT JOIN m.movieGenres mg
            LEFT JOIN m.movieActors ma
            LEFT JOIN m.movieDirectors md2
        WHERE (:moodId     IS NULL OR md.moodId     = :moodId)
          AND (:genreId    IS NULL OR mg.genreId     = :genreId)
          AND (:actorId    IS NULL OR ma.actorId     = :actorId)
          AND (:directorId IS NULL OR md2.directorId = :directorId)
    """)
    Page<Movie> smartSearch(
            @Param("title")      String title,
            @Param("moodId")     Long moodId,
            @Param("genreId")    Long genreId,
            @Param("actorId")    Long actorId,
            @Param("directorId") Long directorId,
            Pageable pageable
    );

    @Query("""
        SELECT m
        FROM Movie m
        WHERE LOWER(m.movieTitle) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Movie> searchByTitle(@Param("q") String q);

    List<Movie> findByMovieMoods_MoodId(Long moodId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_moods WHERE movie_id = :movieId",
            nativeQuery = true
    )
    void deleteMovieMoodsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_genres WHERE movie_id = :movieId",
            nativeQuery = true
    )
    void deleteMovieGenresByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_actors WHERE movie_id = :movieId",
            nativeQuery = true
    )
    void deleteMovieActorsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_directors WHERE movie_id = :movieId",
            nativeQuery = true
    )
    void deleteMovieDirectorsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM user_favorite_movies WHERE movie_id = :movieId",
            nativeQuery = true
    )
    void deleteMovieFansByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_genres WHERE genre_id = :genreId",
            nativeQuery = true
    )
    void deleteMovieGenresByGenreId(@Param("genreId") Long genreId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_actors WHERE actor_id = :actorId",
            nativeQuery = true
    )
    void deleteMovieActorsByActorId(@Param("actorId") Long actorId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM movie_directors WHERE director_id = :directorId",
            nativeQuery = true
    )
    void deleteMovieDirectorsByDirectorId(@Param("directorId") Long directorId);
}