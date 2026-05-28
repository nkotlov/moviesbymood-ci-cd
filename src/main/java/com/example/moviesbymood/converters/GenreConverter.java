package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.GenreDto;
import com.example.moviesbymood.models.Genre;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GenreConverter implements Converter<Genre, GenreDto> {
    @Override
    public GenreDto convert(Genre genre) {
        return GenreDto.builder()
                .genreId(genre.getGenreId())
                .genreName(genre.getGenreName())
                .build();
    }
}
