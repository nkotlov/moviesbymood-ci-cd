package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MovieDto;

import java.util.List;
import java.util.Set;

public interface FavoriteMovieService {
    Set<Long> getFavoriteMovieIds();
    List<MovieDto> getFavoriteMovies();
    boolean isFavoriteMovie(Long movieId);
    void toggleFavoriteMovie(Long movieId);
}
