package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.Comment;
import java.util.List;

public interface CommentService {
    void save(Long movieId, CommentDto dto);
    List<Comment> findByMovie(Long movieId);
    Comment addComment(Long movieId, String userEmail, String text);
    List<CommentDto> findAllComments();
    void deleteById(Long commentId);
}
