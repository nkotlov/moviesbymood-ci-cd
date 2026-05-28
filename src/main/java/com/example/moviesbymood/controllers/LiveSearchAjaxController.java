package com.example.moviesbymood.controllers;

import com.example.moviesbymood.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LiveSearchAjaxController {

    private static final Logger log = LoggerFactory.getLogger(LiveSearchAjaxController.class);

    private final MovieRepository        movieRepo;
    private final GenreRepository        genreRepo;
    private final ActorRepository        actorRepo;
    private final DirectorRepository     directorRepo;
    private final MoodCategoryRepository moodRepo;

    @GetMapping(value = "/search/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> liveSearch(@RequestParam("q") String query) {
        try {
            String q = query.trim().toLowerCase();

            var movies = movieRepo.searchByTitle(q).stream()
                    .map(m -> Map.of("id", m.getMovieId(), "title", m.getMovieTitle()))
                    .collect(Collectors.toList());

            var genres = genreRepo.searchByName(q).stream()
                    .map(g -> Map.of("id", g.getGenreId(), "name", g.getGenreName()))
                    .collect(Collectors.toList());

            var actors = actorRepo.searchByFullName(q).stream()
                    .map(a -> Map.of("id", a.getActorId(), "fullName", a.getActorFullName()))
                    .collect(Collectors.toList());

            var directors = directorRepo.searchByFullName(q).stream()
                    .map(d -> Map.of("id", d.getDirectorId(), "fullName", d.getDirectorFullName()))
                    .collect(Collectors.toList());

            var moods = moodRepo.searchByName(q).stream()
                    .map(mo -> Map.of("id", mo.getMoodId(), "moodName", mo.getMoodName()))
                    .collect(Collectors.toList());

            return Map.of(
                    "movies",    movies,
                    "genres",    genres,
                    "actors",    actors,
                    "directors", directors,
                    "moods",     moods
            );
        } catch (Exception e) {
            log.error("Ошибка при AJAX-поиске для запроса '{}'", query, e);
            throw e;
        }
    }
}
