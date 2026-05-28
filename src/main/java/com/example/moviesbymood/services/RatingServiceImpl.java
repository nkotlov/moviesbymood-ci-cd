package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Rating;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.RatingRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final RatingRepository ratingRepo;
    private final MovieRepository  movieRepo;
    private final UserRepository   userRepo;

    private User findUser(String email) {
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    @Override
    public Rating saveOrUpdate(Long movieId, String userEmail, Short score) {
        try {
            User user = findUser(userEmail);
            Movie movie = movieRepo.findById(movieId)
                    .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + movieId));

            Optional<Rating> existing = ratingRepo
                    .findByRatedMovie_MovieIdAndRatingUser_UserId(movieId, user.getUserId());

            Rating rating = existing.orElseGet(Rating::new);
            rating.setRatedMovie(movie);
            rating.setRatingUser(user);
            rating.setRatingScore(score);
            rating.setRatingCreatedAt(Instant.now());
            return ratingRepo.save(rating);

        } catch (Exception e) {
            log.error("Ошибка при сохранении/обновлении рейтинга фильма id={} для '{}'", movieId, userEmail, e);
            throw e;
        }
    }

    @Override
    public double getAverageScore(Long movieId) {
        try {
            return ratingRepo.findAverageByMovieId(movieId).orElse(0.0);
        } catch (Exception e) {
            log.error("Ошибка при вычислении среднего рейтинга для фильма id={}", movieId, e);
            throw e;
        }
    }

    @Override
    public void save(Long movieId, RatingDto dto) {
        saveOrUpdate(movieId,
                SecurityContextHolder.getContext().getAuthentication().getName(),
                dto.getScore().shortValue());
    }

    @Override
    public Optional<Rating> findByMovieAndUser(Long movieId, String userEmail) {
        try {
            User user = findUser(userEmail);
            return ratingRepo.findByRatedMovie_MovieIdAndRatingUser_UserId(movieId, user.getUserId());
        } catch (Exception e) {
            log.error("Ошибка при поиске рейтинга фильма id={} для '{}'", movieId, userEmail, e);
            throw e;
        }
    }

    @Override
    public List<RatingDto> findByMovieDto(Long movieId) {
        return List.of();
    }
}
