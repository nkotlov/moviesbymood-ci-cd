package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.MoodCategoryDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.services.FileStorageService;
import com.example.moviesbymood.services.MoodCategoryService;
import com.example.moviesbymood.services.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Controller
@RequestMapping("/admin/moods")
@RequiredArgsConstructor
public class MoodCategoryController {

    private static final Logger log = LoggerFactory.getLogger(MoodCategoryController.class);
    private final MoodCategoryService moodService;
    private final MovieService movieService;
    private final FileStorageService fileStorageService;
    private final FileInfoRepository fileInfoRepo;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=moods";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("moodDto", new MoodCategoryDto());
        model.addAttribute("allMovies", movieService.findAll());
        return "moods/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("moodDto") MoodCategoryDto dto,
            BindingResult br,
            @RequestParam("icon") MultipartFile icon,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allMovies", movieService.findAll());
            return "moods/form";
        }
        try {
            MoodCategory m = moodService.create(dto.toEntity());

            if (!icon.isEmpty()) {
                String storedName = fileStorageService.saveFile(icon);
                var fi = fileInfoRepo.findByFileInfoFilename(storedName)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден"));
                m.setMoodIcon(fi);
                moodService.save(m);
            }

            Set<Long> movieIds = dto.getMovieIds();
            if (movieIds != null && !movieIds.isEmpty()) {
                movieService.findAllByIds(movieIds)
                        .forEach(movie -> movie.getMovieMoods().add(m));
                moodService.update(m.getMoodId(), m);
            }

            return "redirect:/admin?tab=moods";

        } catch (Exception e) {
            log.error("Ошибка при создании настроения '{}'", dto.getMoodName(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        try {
            MoodCategory existing = moodService.findById(id);
            model.addAttribute("moodDto", MoodCategoryDto.fromEntity(existing));
            model.addAttribute("allMovies", movieService.findAll());
            return "moods/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования настроения id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("moodDto") MoodCategoryDto dto,
            BindingResult br,
            @RequestParam("icon") MultipartFile icon,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allMovies", movieService.findAll());
            return "moods/form";
        }
        try {
            MoodCategory m = moodService.findById(id);
            m.setMoodName(dto.getMoodName());
            m.setMoodDescription(dto.getMoodDescription());

            if (!icon.isEmpty()) {
                String storedName = fileStorageService.saveFile(icon);
                var fi = fileInfoRepo.findByFileInfoFilename(storedName)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден"));
                m.setMoodIcon(fi);
            }

            m.getMoodMovies().clear();
            Set<Long> movieIds = dto.getMovieIds();
            if (movieIds != null && !movieIds.isEmpty()) {
                movieService.findAllByIds(movieIds)
                        .forEach(movie -> movie.getMovieMoods().add(m));
            }

            moodService.update(id, m);
            return "redirect:/admin?tab=moods";

        } catch (Exception e) {
            log.error("Ошибка при обновлении настроения id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            MoodCategory toDelete = moodService.findById(id);
            toDelete.getMoodMovies().forEach(movie -> movie.getMovieMoods().remove(toDelete));
            moodService.deleteById(id);
            return "redirect:/admin?tab=moods";
        } catch (Exception e) {
            log.error("Ошибка при удалении настроения id={}", id, e);
            throw e;
        }
    }
}
