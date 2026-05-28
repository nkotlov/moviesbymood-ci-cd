package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.Genre;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Long genreId;
    private String genreName;

    public static GenreDto fromEntity(Genre genre) {
        if (genre == null) return null;
        return GenreDto.builder()
                .genreId(genre.getGenreId())
                .genreName(genre.getGenreName())
                .build();
    }
}
