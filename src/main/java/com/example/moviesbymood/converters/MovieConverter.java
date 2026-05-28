package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MovieConverter implements Converter<Movie, MovieDto> {
    @Override
    public MovieDto convert(Movie m) {
        return MovieDto.builder()
                .movieId(m.getMovieId())
                .movieTitle(m.getMovieTitle())
                .movieDescription(m.getMovieDescription())
                .movieReleaseDate(m.getMovieReleaseDate())
                .movieDuration(m.getMovieDuration())
                .moviePoster(m.getMoviePoster() != null
                        ? "/files/" + m.getMoviePoster().getFileInfoFilename()
                        : null)
                .build();
    }
}
