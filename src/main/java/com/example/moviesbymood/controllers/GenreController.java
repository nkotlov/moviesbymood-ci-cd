package com.example.moviesbymood.controllers;

import com.example.moviesbymood.models.Genre;
import com.example.moviesbymood.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/genres")
@RequiredArgsConstructor
public class GenreController {

    private static final Logger log = LoggerFactory.getLogger(GenreController.class);
    private final GenreService genreService;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=genres";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "genres/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("genre") Genre genre,
                         BindingResult br) {
        if (br.hasErrors()) {
            return "genres/form";
        }
        try {
            genreService.create(genre);
            return "redirect:/admin?tab=genres";
        } catch (Exception e) {
            log.error("Ошибка при создании жанра '{}'", genre.getGenreName(), e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("genre", genreService.findById(id));
            return "genres/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке жанра id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("genre") Genre genre,
                         BindingResult br) {
        if (br.hasErrors()) {
            return "genres/form";
        }
        try {
            genreService.update(id, genre);
            return "redirect:/admin?tab=genres";
        } catch (Exception e) {
            log.error("Ошибка при обновлении жанра id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            genreService.deleteById(id);
            return "redirect:/admin?tab=genres";
        } catch (Exception e) {
            log.error("Ошибка при удалении жанра id={}", id, e);
            throw e;
        }
    }
}
