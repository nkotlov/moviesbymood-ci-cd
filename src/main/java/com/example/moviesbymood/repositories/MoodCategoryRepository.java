package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.MoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MoodCategoryRepository extends JpaRepository<MoodCategory, Long> {

    @Modifying
    @Query(value = "DELETE FROM movie_moods WHERE mood_id = :moodId", nativeQuery = true)
    void deleteMovieMoodsByMoodId(@Param("moodId") Long moodId);

    @Modifying
    @Query(value = "DELETE FROM mood_ratings WHERE mood_id = :moodId", nativeQuery = true)
    void deleteMoodRatingsByMoodId(@Param("moodId") Long moodId);

    @Modifying
    @Query(value = "DELETE FROM user_favorite_moods WHERE mood_id = :moodId", nativeQuery = true)
    void deleteFavoriteMoodsByMoodId(@Param("moodId") Long moodId);

    @Modifying
    @Query(value = "DELETE FROM mood_comments WHERE mood_id = :moodId", nativeQuery = true)
    void deleteMoodCommentsByMoodId(@Param("moodId") Long moodId);

    List<MoodCategory> findByMoodNameContainingIgnoreCase(String namePart);

    @Query("""
       SELECT mo
         FROM MoodCategory mo
        WHERE LOWER(mo.moodName) LIKE LOWER(CONCAT('%', :q, '%'))
      """)
    List<MoodCategory> searchByName(@Param("q") String q);
}
