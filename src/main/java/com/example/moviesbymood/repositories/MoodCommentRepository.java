package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.MoodComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoodCommentRepository extends JpaRepository<MoodComment, Long> {
    List<MoodComment> findAllByOrderByMoodCommentCreatedAtDesc();
    List<MoodComment> findByCommentMood_MoodIdOrderByMoodCommentCreatedAtDesc(Long moodId);
    void deleteByCommentMood_MoodId(Long moodId);
}
