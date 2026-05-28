package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM movie_genres WHERE genre_id = :genreId", nativeQuery = true)
    void deleteMovieGenresByGenreId(@Param("genreId") Long genreId);

    @Query("""
       SELECT g
         FROM Genre g
        WHERE LOWER(g.genreName) LIKE LOWER(CONCAT('%', :q, '%'))
      """)
    List<Genre> searchByName(@Param("q") String q);

    List<Genre> findByGenreNameContainingIgnoreCase(String namePart);
}