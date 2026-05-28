package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteMovieServiceImpl implements FavoriteMovieService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteMovieServiceImpl.class);

    private final UserRepository              userRepo;
    private final MovieRepository             movieRepo;
    private final Converter<Movie, MovieDto>  movieConverter;

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getFavoriteMovieIds() {
        try {
            return currentUser().getFavoriteMovies()
                    .stream()
                    .map(Movie::getMovieId)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Ошибка при получении id избранных фильмов", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getFavoriteMovies() {
        try {
            return getFavoriteMovieIds().stream()
                    .map(movieRepo::getReferenceById)
                    .map(movieConverter::convert)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении списка избранных фильмов", e);
            throw e;
        }
    }

    @Override
    public boolean isFavoriteMovie(Long movieId) {
        try {
            return getFavoriteMovieIds().contains(movieId);
        } catch (Exception e) {
            log.error("Ошибка при проверке избранности фильма id={}", movieId, e);
            throw e;
        }
    }

    @Override
    public void toggleFavoriteMovie(Long movieId) {
        try {
            User user = currentUser();
            boolean removed = user.getFavoriteMovies().removeIf(m -> m.getMovieId().equals(movieId));
            if (!removed) {
                Movie proxy = new Movie();
                proxy.setMovieId(movieId);
                user.getFavoriteMovies().add(proxy);
            }
            userRepo.save(user);
        } catch (Exception e) {
            log.error("Ошибка при переключении избранного фильма id={}", movieId, e);
            throw e;
        }
    }
}
