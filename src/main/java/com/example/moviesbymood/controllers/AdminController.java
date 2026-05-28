package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.*;
import com.example.moviesbymood.services.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final MovieService        movieService;
    private final GenreService        genreService;
    private final ActorService        actorService;
    private final DirectorService     directorService;
    private final MoodCategoryService moodCategoryService;
    private final UserService         userService;
    private final CommentService      commentService;
    private final MoodCommentService  moodCommentService;
    private final OmdbService         omdbService;
    private final FileStorageService  fileStorageService;

    @GetMapping
    public String dashboard(
            @RequestParam(name = "tab", required = false, defaultValue = "movies") String activeTab,
            Model model) {
        try {
            model.addAttribute("activeTab", activeTab);
            model.addAttribute("movies", movieService.findAllVisible());
            model.addAttribute("newMovieDto", new MovieDto());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("newGenre", new GenreDto());
            model.addAttribute("actors", actorService.findAll());
            model.addAttribute("newActor", new ActorDto());
            model.addAttribute("directors", directorService.findAll());
            model.addAttribute("newDirector", new DirectorDto());
            model.addAttribute("moods", moodCategoryService.findAllDto());
            model.addAttribute("newMood", new MoodCategoryDto());
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("comments", commentService.findAllComments());
            model.addAttribute("moodComments", moodCommentService.findAllComments());
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("Ошибка при загрузке админ-панели, вкладка={}", activeTab, e);
            throw e;
        }
    }

    @GetMapping("/import")
    public String showImportForm(Model model) {
        try {
            model.addAttribute("title", "");
            return "admin/import";
        } catch (Exception e) {
            log.error("Ошибка при отображении формы импорта", e);
            throw e;
        }
    }

    @PostMapping("/import")
    public String importByTitle(@RequestParam String title, Model model) {
        log.info("Importing movie from OMDb by title='{}'", title);

        OmdbResponse dto;
        try {
            dto = omdbService.fetchByTitle(title);
            if (!"True".equalsIgnoreCase(dto.getResponse())) {
                log.warn("OMDb response error: {}", dto.getError());
                model.addAttribute("error", dto.getError());
                model.addAttribute("title", title);
                return "admin/import";
            }
        } catch (Exception e) {
            log.error("Error fetching from OMDb for title='{}': {}", title, e.getMessage(), e);
            model.addAttribute("error", "Ошибка OMDb: " + e.getMessage());
            model.addAttribute("title", title);
            return "admin/import";
        }

        String storedFilename = null;
        String posterUrl = dto.getPoster();
        if (posterUrl != null && !posterUrl.isBlank() && !"N/A".equals(posterUrl)) {
            try {
                log.debug("Downloading poster from URL: {}", posterUrl);
                URL url = new URL(posterUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                try (InputStream in = conn.getInputStream()) {
                    String ext = posterUrl.toLowerCase().endsWith(".jpg") ? ".jpg" : ".png";
                    storedFilename = UUID.randomUUID() + ext;
                    Path target = Path.of(fileStorageService.getStoragePath(), storedFilename);
                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                    log.debug("Poster saved to {}", target);
                    fileStorageService.registerFile(storedFilename, conn.getContentType(), target);
                    log.info("Registered poster file '{}'", storedFilename);
                }
            } catch (Exception ex) {
                log.warn("Failed to download or save poster for '{}': {}", title, ex.getMessage(), ex);
            }
        }

        int duration = 0;
        try {
            duration = Integer.parseInt(dto.getRuntime().replaceAll("\\D+", ""));
        } catch (Exception ignored) {
            log.debug("Could not parse runtime '{}' for title='{}'", dto.getRuntime(), title);
        }

        MovieDto movie = MovieDto.builder()
                .movieTitle(dto.getTitle())
                .movieDuration(duration)
                .moviePoster(storedFilename)
                .build();
        movieService.create(movie);
        return "redirect:/admin?tab=movies";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long userId,
                             @RequestParam(name="tab", defaultValue="users") String tab) {
        try {
            userService.deleteById(userId);
            return "redirect:/admin?tab=" + tab;
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя id={}", userId, e);
            throw e;
        }
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable("id") Long commentId,
                                @RequestParam(name="tab", defaultValue="comments") String tab) {
        try {
            commentService.deleteById(commentId);
            return "redirect:/admin?tab=" + tab;
        } catch (Exception e) {
            log.error("Ошибка при удалении отзыва id={}", commentId, e);
            throw e;
        }
    }

    @PostMapping("/mood-comments/{id}/delete")
    public String deleteMoodComment(@PathVariable("id") Long moodCommentId,
                                    @RequestParam(name="tab", defaultValue="moodComments") String tab) {
        try {
            moodCommentService.deleteById(moodCommentId);
            return "redirect:/admin?tab=" + tab;
        } catch (Exception e) {
            log.error("Ошибка при удалении комментария к настроению id={}", moodCommentId, e);
            throw e;
        }
    }
}
