package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.MoodRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MoodRatingRepository extends JpaRepository<MoodRating, Long> {

    Optional<MoodRating> findByRatedMood_MoodIdAndRatingUser_UserId(Long moodId, Long userId);

    @Query("SELECT AVG(m.moodRatingScore) FROM MoodRating m WHERE m.ratedMood.moodId = :moodId")
    Optional<Double> findAverageByMoodId(@Param("moodId") Long moodId);
}
