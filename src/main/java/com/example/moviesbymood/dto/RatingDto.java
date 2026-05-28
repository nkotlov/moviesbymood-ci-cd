package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.Rating;
import com.example.moviesbymood.models.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long ratingId;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer score;

    private LocalDateTime createdAt;

    private Long userId;
    private String userNickname;

    public static RatingDto fromEntity(Rating rating) {
        if (rating == null) return null;

        User user = rating.getRatingUser();

        Instant createdInstant = rating.getRatingCreatedAt();
        LocalDateTime createdAt = createdInstant == null
                ? null
                : LocalDateTime.ofInstant(createdInstant, ZoneId.systemDefault());

        return RatingDto.builder()
                .ratingId(rating.getRatingId())
                .score(rating.getRatingScore().intValue())
                .createdAt(createdAt)
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .build();
    }
}
