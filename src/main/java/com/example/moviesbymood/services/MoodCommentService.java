package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.MoodComment;
import java.util.List;

public interface MoodCommentService {
    List<MoodComment> findByMood(Long moodId);
    MoodComment saveForMood(Long moodId, String userEmail, String text);
    List<CommentDto> findAllComments();
    void deleteById(Long id);
}
