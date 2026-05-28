package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;

public interface MoodRatingService {
    Short getUserRating(Long moodId, String userEmail);
    double getAverageMoodScore(Long moodId);
    void saveForMood(Long moodId, RatingDto dto);
}
