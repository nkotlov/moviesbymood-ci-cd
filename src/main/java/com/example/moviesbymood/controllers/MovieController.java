package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {
    private static final Logger log = LoggerFactory.getLogger(MovieController.class);

    private final MovieService        movieService;
    private final GenreService        genreService;
    private final ActorService        actorService;
    private final DirectorService     directorService;
    private final FileStorageService  fileStorageService;
    private final FileInfoRepository  fileInfoRepository;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=movies";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        MovieDto dto = new MovieDto();
        dto.setGenreIds(Collections.emptySet());
        dto.setActorIds(Collections.emptySet());
        dto.setDirectorIds(Collections.emptySet());
        model.addAttribute("movieDto", dto);
        loadLists(model);
        return "movies/form";
    }

    @GetMapping({"/{id}", "/{id}/edit"})
    public String editForm(@PathVariable Long id, Model model) {
        try {
            MovieDto dto = movieService.findDtoById(id);
            if (dto.getGenreIds()    == null) dto.setGenreIds(new HashSet<>());
            if (dto.getActorIds()    == null) dto.setActorIds(new HashSet<>());
            if (dto.getDirectorIds() == null) dto.setDirectorIds(new HashSet<>());
            model.addAttribute("movieDto", dto);
            loadLists(model);
            return "movies/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования фильма id={}", id, e);
            throw e;
        }
    }

    private void loadLists(Model model) {
        model.addAttribute("allGenres",    genreService.findAll());
        model.addAttribute("allActors",    actorService.findAll());
        model.addAttribute("allDirectors", directorService.findAll());
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("movieDto") MovieDto dto,
            BindingResult br,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("allGenres",    genreService.findAll());
            model.addAttribute("allActors",    actorService.findAll());
            model.addAttribute("allDirectors", directorService.findAll());
            return "movies/form";
        }

        try {
            Movie movie = movieService.create(dto);

            if (posterFile != null && !posterFile.isEmpty()) {
                String stored = fileStorageService.saveFile(posterFile);
                FileInfo fi = fileInfoRepository.findByFileInfoFilename(stored)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + stored));
                movie.setMoviePoster(fi);
                movieService.save(movie);
            }

            return "redirect:/admin?tab=movies";

        } catch (Exception e) {
            log.error("Ошибка при создании фильма '{}'", dto.getMovieTitle(), e);
            throw e;
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("movieDto") MovieDto dto,
            BindingResult br,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            Model model
    ) throws IOException {
        if (br.hasErrors()) {
            loadLists(model);
            return "movies/form";
        }

        try {
            if (posterFile != null && !posterFile.isEmpty()) {
                String stored = fileStorageService.saveFile(posterFile);
                dto.setMoviePoster(stored);
            }
            movieService.update(id, dto);
            return "redirect:/admin?tab=movies";

        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            movieService.delete(id);
            return "redirect:/admin?tab=movies";
        } catch (Exception e) {
            log.error("Ошибка при удалении фильма id={}", id, e);
            throw e;
        }
    }
}
