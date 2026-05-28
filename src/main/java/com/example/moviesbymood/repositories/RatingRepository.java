package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByRatedMovie_MovieIdAndRatingUser_UserId(Long movieId, Long userId);

    @Query("SELECT AVG(r.ratingScore) FROM Rating r WHERE r.ratedMovie.movieId = :movieId")
    Optional<Double> findAverageByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Rating r WHERE r.ratedMovie.movieId = :movieId")
    void deleteByRatedMovie_MovieId(@Param("movieId") Long movieId);
}
