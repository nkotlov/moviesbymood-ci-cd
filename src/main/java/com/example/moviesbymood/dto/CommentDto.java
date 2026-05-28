package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.Comment;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.User;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long commentId;
    private String commentText;
    private LocalDateTime commentCreatedAt;
    private Long userId;
    private String userEmail;
    private String userNickname;
    private String contextType;
    private Long contextId;
    private String contextTitle;

    public static CommentDto fromEntity(Comment comment) {
        if (comment == null) return null;

        User user = comment.getCommentUser();
        Movie movie = comment.getCommentMovie();

        Instant createdInstant = comment.getCommentCreatedAt();
        LocalDateTime createdAt = createdInstant == null
                ? null
                : LocalDateTime.ofInstant(createdInstant, ZoneId.systemDefault());

        return CommentDto.builder()
                .commentId(comment.getCommentId())
                .commentText(comment.getCommentText())
                .commentCreatedAt(createdAt)
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .contextType("MOVIE")
                .contextId(movie.getMovieId())
                .contextTitle(movie.getMovieTitle())
                .build();
    }
}
