package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.Comment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CommentConverter implements Converter<Comment, CommentDto> {
    @Override
    public CommentDto convert(Comment c) {
        LocalDateTime ldt = LocalDateTime.ofInstant(
                c.getCommentCreatedAt(), ZoneId.systemDefault());

        return CommentDto.builder()
                .commentId(c.getCommentId())
                .commentText(c.getCommentText())
                .commentCreatedAt(ldt)
                .userId(c.getCommentUser().getUserId())
                .userNickname(c.getCommentUser().getUserNickname())
                .build();
    }
}
