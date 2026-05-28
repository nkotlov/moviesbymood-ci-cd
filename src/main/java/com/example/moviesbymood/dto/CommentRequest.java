package com.example.moviesbymood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String commentText;
}
