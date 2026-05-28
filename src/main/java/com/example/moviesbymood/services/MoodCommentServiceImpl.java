package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.MoodComment;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.MoodCommentRepository;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MoodCommentServiceImpl implements MoodCommentService {

    private static final Logger log = LoggerFactory.getLogger(MoodCommentServiceImpl.class);

    private final MoodCommentRepository  moodCommentRepository;
    private final MoodCategoryRepository moodCategoryRepository;
    private final UserRepository         userRepository;

    private User currentUser() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
    }

    @Override
    public List<MoodComment> findByMood(Long moodId) {
        try {
            return moodCommentRepository.findByCommentMood_MoodIdOrderByMoodCommentCreatedAtDesc(moodId);
        } catch (Exception e) {
            log.error("Ошибка при получении комментариев настроения id={}", moodId, e);
            throw e;
        }
    }

    @Override
    public MoodComment saveForMood(Long moodId, String userEmail, String text) {
        try {
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userEmail));
            MoodCategory mood = moodCategoryRepository.findById(moodId)
                    .orElseThrow(() -> new IllegalArgumentException("Настроение не найдено: " + moodId));
            MoodComment mc = new MoodComment();
            mc.setCommentUser(user);
            mc.setCommentMood(mood);
            mc.setMoodCommentText(text);
            mc.setMoodCommentCreatedAt(Instant.now());
            return moodCommentRepository.save(mc);
        } catch (Exception e) {
            log.error("Ошибка при добавлении комментария к настроению id={} от {}", moodId, userEmail, e);
            throw e;
        }
    }

    @Override
    public List<CommentDto> findAllComments() {
        try {
            return moodCommentRepository.findAllByOrderByMoodCommentCreatedAtDesc()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении всех комментариев настроений", e);
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            moodCommentRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении комментария настроения id={}", id, e);
            throw e;
        }
    }

    private CommentDto toDto(MoodComment mc) {
        var ldt = LocalDateTime.ofInstant(mc.getMoodCommentCreatedAt(), ZoneId.systemDefault());
        return CommentDto.builder()
                .commentId(mc.getMoodCommentId())
                .commentText(mc.getMoodCommentText())
                .commentCreatedAt(ldt)
                .userId(mc.getCommentUser().getUserId())
                .userNickname(mc.getCommentUser().getUserNickname())
                .contextType("MOOD")
                .contextId(mc.getCommentMood().getMoodId())
                .contextTitle(mc.getCommentMood().getMoodName())
                .build();
    }
}
