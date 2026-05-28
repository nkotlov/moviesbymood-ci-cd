package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CommentRequest;
import com.example.moviesbymood.dto.RatingRequest;
import com.example.moviesbymood.models.Comment;
import com.example.moviesbymood.models.Rating;
import com.example.moviesbymood.services.CommentService;
import com.example.moviesbymood.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movies/{movieId}")
@RequiredArgsConstructor
@Validated
public class AjaxCsrfController {

    private static final Logger log = LoggerFactory.getLogger(AjaxCsrfController.class);

    private final RatingService  ratingService;
    private final CommentService commentService;

    @PostMapping("/rate")
    public ResponseEntity<?> rateMovie(
            @PathVariable Long movieId,
            @RequestBody @Validated RatingRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        try {
            Rating updated = ratingService.saveOrUpdate(movieId, user.getUsername(), req.getScore());
            double avg = ratingService.getAverageScore(movieId);
            return ResponseEntity.ok(Map.of(
                    "userScore", updated.getRatingScore(),
                    "averageScore", avg
            ));
        } catch (Exception e) {
            log.error("Ошибка AJAX-оценки фильма id={}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<?> commentMovie(
            @PathVariable Long movieId,
            @RequestBody @Validated CommentRequest req,
            @AuthenticationPrincipal UserDetails user
    ) {
        try {
            Comment c = commentService.addComment(movieId, user.getUsername(), req.getCommentText());
            return ResponseEntity.ok(c);
        } catch (Exception e) {
            log.error("Ошибка AJAX-комментария к фильму id={}", movieId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
