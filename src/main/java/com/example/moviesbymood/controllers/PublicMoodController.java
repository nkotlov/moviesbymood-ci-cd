package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.MoodCategoryRepository;
import com.example.moviesbymood.repositories.UserRepository;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/moods")
@RequiredArgsConstructor
public class PublicMoodController {
    private static final Logger log = LoggerFactory.getLogger(PublicMoodController.class);

    private final MoodCategoryService moodCategoryService;
    private final MoodCommentService   moodCommentService;
    private final MoodRatingService    moodRatingService;
    private final MovieService         movieService;
    private final UserRepository       userRepository;
    private final MoodCategoryRepository moodCategoryRepository;
    private final PlaylistService        playlistService;

    @GetMapping
    public String catalog(Model model, Principal principal) {
        try {
            List<MoodCategoryDto> moods = moodCategoryService.findAll();
            model.addAttribute("moods", moods);
            if (principal != null) {
                model.addAttribute("userPlaylists",
                        playlistService.getUserPlaylists(principal.getName()));
            }
            model.addAttribute("userFavoriteMoodIds",
                    moodCategoryService.getFavoriteMoodIds());
            model.addAttribute("activeTab", "moods");
            return "moods/catalog";
        } catch (Exception e) {
            log.error("Ошибка при загрузке каталога настроений", e);
            throw e;
        }
    }

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name = "fromProfile", required = false) Boolean fromProfile,
            Model model,
            Principal principal
    ) {
        try {
            MoodCategory entity = moodCategoryService.findEntity(id);
            model.addAttribute("mood", MoodCategoryDto.fromEntity(entity));
            boolean isFav = principal != null && moodCategoryService.isFavorite(id);
            model.addAttribute("isFavoriteMood", isFav);
            model.addAttribute("comments", moodCommentService.findByMood(id));
            model.addAttribute("averageRating", moodRatingService.getAverageMoodScore(id));
            model.addAttribute("newComment", new CommentRequest());
            model.addAttribute("newRating",  new RatingDto());
            Short userRating = principal == null ? null :
                    moodRatingService.getUserRating(id, principal.getName());
            model.addAttribute("userRating", userRating);
            model.addAttribute("moodMovies", movieService.findByMood(id));
            model.addAttribute("fromProfile", fromProfile != null && fromProfile);
            model.addAttribute("activeTab",    "moods");
            return "moods/detail";
        } catch (Exception e) {
            log.error("Ошибка при отображении детали настроения id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/favorite")
    @ResponseBody
    public ResponseEntity<Void> toggleFavoriteMood(
            @PathVariable Long id,
            Principal principal
    ) {
        try {
            if (principal == null) {
                throw new ResponseStatusException(UNAUTHORIZED);
            }
            User user = userRepository.findByUserEmail(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            MoodCategory moodEntity = moodCategoryRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
            moodCategoryService.toggle(id, user, moodEntity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при переключении избранного настроения id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/comments")
    public String addComment(
            @PathVariable Long id,
            @ModelAttribute("newComment") @Valid CommentRequest requestDto,
            BindingResult br,
            Principal principal
    ) {
        try {
            if (!br.hasErrors() && principal != null) {
                moodCommentService.saveForMood(id, principal.getName(), requestDto.getCommentText());
            }
            return "redirect:/moods/" + id;
        } catch (Exception e) {
            log.error("Ошибка при добавлении комментария к настроению id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/rating")
    public String saveRating(
            @PathVariable Long id,
            @ModelAttribute("newRating") @Valid RatingDto dto,
            BindingResult br,
            Principal principal
    ) {
        try {
            if (!br.hasErrors() && principal != null) {
                moodRatingService.saveForMood(id, dto);
            }
            return "redirect:/moods/" + id;
        } catch (Exception e) {
            log.error("Ошибка при сохранении рейтинга настроения id=" + id, e);
            throw e;
        }
    }
}
