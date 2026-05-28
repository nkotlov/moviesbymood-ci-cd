package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.MoodRating;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.MoodRatingRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MoodRatingServiceImpl implements MoodRatingService {

    private static final Logger log = LoggerFactory.getLogger(MoodRatingServiceImpl.class);

    private final MoodRatingRepository   moodRatingRepo;
    private final MoodCategoryRepository moodCategoryRepo;
    private final UserRepository         userRepo;

    private User findUserByEmail(String email) {
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    private MoodCategory findMoodById(Long moodId) {
        return moodCategoryRepo.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("Настроение не найдено: " + moodId));
    }

    @Override
    public Short getUserRating(Long moodId, String userEmail) {
        try {
            User user = findUserByEmail(userEmail);
            Optional<MoodRating> opt = moodRatingRepo
                    .findByRatedMood_MoodIdAndRatingUser_UserId(moodId, user.getUserId());
            return opt.map(MoodRating::getMoodRatingScore).orElse(null);
        } catch (Exception e) {
            log.error("Ошибка при получении оценки пользователя {} для настроения id={}", userEmail, moodId, e);
            throw e;
        }
    }

    @Override
    public double getAverageMoodScore(Long moodId) {
        try {
            return moodRatingRepo.findAverageByMoodId(moodId).orElse(0.0);
        } catch (Exception e) {
            log.error("Ошибка при вычислении средней оценки настроения id={}", moodId, e);
            throw e;
        }
    }

    @Override
    public void saveForMood(Long moodId, RatingDto dto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = findUserByEmail(email);
            MoodCategory mood = findMoodById(moodId);
            Optional<MoodRating> existing = moodRatingRepo
                    .findByRatedMood_MoodIdAndRatingUser_UserId(moodId, user.getUserId());

            MoodRating rating = existing.map(r -> {
                r.setMoodRatingScore(dto.getScore().shortValue());
                r.setMoodRatingCreatedAt(Instant.now());
                return r;
            }).orElseGet(() -> MoodRating.builder()
                    .ratedMood(mood)
                    .ratingUser(user)
                    .moodRatingScore(dto.getScore().shortValue())
                    .moodRatingCreatedAt(Instant.now())
                    .build());

            moodRatingRepo.save(rating);
        } catch (Exception e) {
            log.error("Ошибка при сохранении оценки для настроения id={}", moodId, e);
            throw e;
        }
    }
}
