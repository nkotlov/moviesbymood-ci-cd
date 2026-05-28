package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.MoodCategoryDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodCategoryServiceImpl implements MoodCategoryService {

    private final MoodCategoryRepository moodRepo;
    private final UserRepository         userRepo;

    @Override
    public MoodCategory create(MoodCategory mood) {
        return moodRepo.save(mood);
    }

    @Override
    public MoodCategory update(Long id, MoodCategory mood) {
        MoodCategory existing = moodRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
        existing.setMoodName(mood.getMoodName());
        existing.setMoodDescription(mood.getMoodDescription());
        return moodRepo.save(existing);
    }

    @Override
    public MoodCategory save(MoodCategory mood) {
        return moodRepo.save(mood);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        moodRepo.deleteMovieMoodsByMoodId(id);
        moodRepo.deleteMoodRatingsByMoodId(id);
        moodRepo.deleteFavoriteMoodsByMoodId(id);
        moodRepo.deleteMoodCommentsByMoodId(id);
        moodRepo.deleteById(id);
    }

    @Override
    public MoodCategoryDto findDtoById(Long id) {
        MoodCategory m = moodRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
        return MoodCategoryDto.fromEntity(m);
    }

    @Override
    public MoodCategory findById(Long id) {
        return moodRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Настроение не найдено: " + id));
    }

    @Override
    public MoodCategory findEntity(Long id) {
        return findById(id);
    }

    @Override
    public List<MoodCategoryDto> findAllDto() {
        return moodRepo.findAll().stream()
                .map(MoodCategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoodCategoryDto> findAll() {
        return findAllDto();
    }

    @Override
    public MoodCategoryDto create(MoodCategoryDto dto) {
        MoodCategory m = new MoodCategory();
        m.setMoodName(dto.getMoodName());
        m.setMoodDescription(dto.getMoodDescription());
        MoodCategory saved = moodRepo.save(m);
        return MoodCategoryDto.fromEntity(saved);
    }

    @Override
    public Optional<MoodCategoryDto> update(Long id, MoodCategoryDto dto) {
        return moodRepo.findById(id).map(existing -> {
            existing.setMoodName(dto.getMoodName());
            existing.setMoodDescription(dto.getMoodDescription());
            MoodCategory saved = moodRepo.save(existing);
            return MoodCategoryDto.fromEntity(saved);
        });
    }

    @Override
    public boolean delete(Long id) {
        if (!moodRepo.existsById(id)) {
            return false;
        }
        MoodCategory toDelete = moodRepo.findById(id).orElseThrow();
        toDelete.getMoodMovies().forEach(movie -> movie.getMovieMoods().remove(toDelete));
        moodRepo.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public void toggle(Long id, User ignoredUser, MoodCategory ignoredMood) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + email));
        MoodCategory mood = findEntity(id);

        if (user.getFavoriteMoods().contains(mood)) {
            user.getFavoriteMoods().remove(mood);
        } else {
            user.getFavoriteMoods().add(mood);
        }
        userRepo.save(user);
    }

    @Override
    public boolean isFavorite(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .map(u -> u.getFavoriteMoods().stream()
                        .anyMatch(m -> m.getMoodId().equals(id)))
                .orElse(false);
    }

    @Override
    public Object getFavoriteMoodIds() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserEmail(email)
                .map(u -> u.getFavoriteMoods().stream()
                        .map(MoodCategory::getMoodId)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoodCategoryDto> searchByName(String namePart) {
        return moodRepo
                .findByMoodNameContainingIgnoreCase(namePart)
                .stream()
                .map(MoodCategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}
