package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CommentDto;
import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.dto.RatingDto;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequestMapping("/movies")
@RequiredArgsConstructor
public class PublicMovieController {
    private static final Logger log = LoggerFactory.getLogger(PublicMovieController.class);

    private final MovieService         movieService;
    private final FavoriteMovieService favoriteMovieService;
    private final CommentService       commentService;
    private final RatingService        ratingService;
    private final MoodCategoryService  moodCategoryService;
    private final GenreService         genreService;
    private final ActorService         actorService;
    private final DirectorService      directorService;

    @GetMapping
    public String catalog(
            @RequestParam(name="movieTitle", required=false) String movieTitle,
            @RequestParam(name="mood",       required=false) Long moodId,
            @RequestParam(name="genre",      required=false) Long genreId,
            @RequestParam(name="actor",      required=false) Long actorId,
            @RequestParam(name="director",   required=false) Long directorId,
            @RequestParam(required=false, defaultValue="") String ratingSort,
            @RequestHeader(value="X-Requested-With", required=false) String requestedWith,
            Model model,
            Principal principal
    ) {
        try {
            PageRequest pageReq = PageRequest.of(0, 100);
            Page<Movie> page = movieService.smartSearch(
                    movieTitle, moodId, genreId, actorId, directorId, pageReq
            );
            List<MovieDto> movies = page.stream()
                    .map(m -> movieService.findDtoById(m.getMovieId()))
                    .collect(Collectors.toList());

            if ("asc".equals(ratingSort)) {
                movies.sort(Comparator.comparing(MovieDto::getMovieAverageRating,
                        Comparator.nullsLast(Double::compareTo)));
            } else if ("desc".equals(ratingSort)) {
                movies.sort(Comparator.comparing(MovieDto::getMovieAverageRating,
                        Comparator.nullsLast(Double::compareTo)).reversed());
            }

            model.addAttribute("movies", movies);
            model.addAttribute("filterTitle", movieTitle);
            model.addAttribute("filterMood",   moodId);
            model.addAttribute("filterGenre",  genreId);
            model.addAttribute("filterActor",  actorId);
            model.addAttribute("filterDirector", directorId);
            model.addAttribute("ratingSort",   ratingSort);
            model.addAttribute("allMoods",     moodCategoryService.findAllDto());
            model.addAttribute("allGenres",    genreService.findAllDto());
            model.addAttribute("allActors",    actorService.findAllDto());
            model.addAttribute("allDirectors", directorService.findAllDto());

            Set<Long> userFavIds = principal == null
                    ? Collections.emptySet()
                    : favoriteMovieService.getFavoriteMovieIds();
            model.addAttribute("userFavorites", userFavIds);

            if ("XMLHttpRequest".equals(requestedWith)) {
                return "movies/catalog :: moviesList";
            }
            return "movies/catalog";
        } catch (Exception e) {
            log.error("Ошибка при загрузке каталога фильмов", e);
            throw e;
        }
    }

    @GetMapping("/{id:\\d+}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(name="fromMood",     required=false) Long fromMood,
            @RequestParam(name="fromProfile",  required=false) Boolean fromProfile,
            @RequestParam(name="fromPlaylist", required=false) Long fromPlaylist,
            Model model,
            Principal principal
    ) {
        try {
            MovieDto movieDto = movieService.findDtoById(id);
            model.addAttribute("movie", movieDto);

            boolean isFav = principal != null && favoriteMovieService.isFavoriteMovie(id);
            model.addAttribute("isFavorite", isFav);

            model.addAttribute("fromMood",     fromMood);
            model.addAttribute("fromProfile",  fromProfile != null && fromProfile);
            model.addAttribute("fromPlaylist", fromPlaylist);

            model.addAttribute("comments", commentService.findByMovie(id));
            model.addAttribute("averageRating", ratingService.getAverageScore(id));
            model.addAttribute("newComment", new CommentDto());
            model.addAttribute("newRating",  new RatingDto());
            Integer userRating = principal == null
                    ? null
                    : ratingService.getUserRating(id, principal.getName());
            model.addAttribute("userRating", userRating);
            model.addAttribute("activeTab", "movies");
            return "movies/detail";
        } catch (Exception e) {
            log.error("Ошибка при отображении детали фильма id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id:\\d+}/favorite")
    @ResponseBody
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(UNAUTHORIZED).build();
            }
            favoriteMovieService.toggleFavoriteMovie(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при переключении избранного фильма id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id:\\d+}/comments")
    public String addComment(
            @PathVariable Long id,
            @ModelAttribute("newComment") @Valid CommentDto dto,
            BindingResult br
    ) {
        try {
            if (!br.hasErrors()) {
                commentService.save(id, dto);
            }
            return "redirect:/movies/" + id;
        } catch (Exception e) {
            log.error("Ошибка при добавлении комментария к фильму id=" + id, e);
            throw e;
        }
    }

    @PostMapping("/{id:\\d+}/rating")
    public String saveRating(
            @PathVariable Long id,
            @ModelAttribute("newRating") @Valid RatingDto dto,
            BindingResult br,
            Principal principal
    ) {
        try {
            if (principal != null && !br.hasErrors()) {
                ratingService.save(id, dto);
            }
            return "redirect:/movies/" + id;
        } catch (Exception e) {
            log.error("Ошибка при сохранении рейтинга фильма id=" + id, e);
            throw e;
        }
    }
}
