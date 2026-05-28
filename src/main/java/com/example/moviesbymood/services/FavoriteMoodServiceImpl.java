package com.example.moviesbymood.services;

import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteMoodServiceImpl implements FavoriteMoodService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteMoodServiceImpl.class);

    private final UserRepository         userRepo;
    private final MoodCategoryRepository moodRepo;

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    @Override
    public Set<Long> getFavoriteMoodIds() {
        try {
            return currentUser().getFavoriteMoods()
                    .stream()
                    .map(MoodCategory::getMoodId)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Ошибка при получении id избранных настроений", e);
            throw e;
        }
    }

    @Override
    public List<MoodCategory> getFavoriteMoods() {
        try {
            return currentUser().getFavoriteMoods().stream().toList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка избранных настроений", e);
            throw e;
        }
    }

    @Override
    public boolean isFavoriteMood(Long moodId) {
        try {
            return currentUser().getFavoriteMoods()
                    .stream()
                    .anyMatch(m -> m.getMoodId().equals(moodId));
        } catch (Exception e) {
            log.error("Ошибка при проверке избранности настроения id={}", moodId, e);
            throw e;
        }
    }

    @Override
    public void toggleFavoriteMood(Long moodId) {
        try {
            User user = currentUser();
            MoodCategory mood = moodRepo.getReferenceById(moodId);
            if (!user.getFavoriteMoods().remove(mood)) {
                user.getFavoriteMoods().add(mood);
            }
            userRepo.save(user);
        } catch (Exception e) {
            log.error("Ошибка при переключении избранного настроения id={}", moodId, e);
            throw e;
        }
    }
}
