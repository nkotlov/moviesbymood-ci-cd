package com.example.moviesbymood.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    @Min(value = 1, message = "Оценка ≥1")
    @Max(value = 10, message = "Оценка ≤10")
    private Short score;
}
