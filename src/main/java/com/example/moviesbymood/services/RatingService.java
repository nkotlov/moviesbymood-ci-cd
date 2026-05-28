package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingService {
    Rating saveOrUpdate(Long movieId, String userEmail, Short score);
    double getAverageScore(Long movieId);
    List<RatingDto> findByMovieDto(Long movieId);
    void save(Long movieId, RatingDto dto);
    Optional<Rating> findByMovieAndUser(Long movieId, String userEmail);

    default Integer getUserRating(Long movieId, String userEmail) {
        return findByMovieAndUser(movieId, userEmail)
                .map(Rating::getRatingScore)
                .map(Short::intValue)
                .orElse(null);
    }
}