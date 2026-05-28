package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.services.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistRestController {
    private static final Logger log = LoggerFactory.getLogger(PlaylistRestController.class);
    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @Valid CreatePlaylistRequest req,
            BindingResult br,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().body(br.getAllErrors());
        }
        try {
            PlaylistDto dto = playlistService.createPlaylist(principal.getName(), req);
            return ResponseEntity.created(URI.create("/api/playlists/" + dto.getPlaylistId())).body(dto);
        } catch (Exception e) {
            log.error("Ошибка API при создании плейлиста", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> all(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            List<PlaylistDto> list = playlistService.getUserPlaylists(principal.getName());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("Ошибка API при получении плейлистов", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            return ResponseEntity.ok(playlistService.getPlaylistById(id, principal.getName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка API при получении плейлиста id={}", id, e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            playlistService.deletePlaylist(id, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка API при удалении плейлиста id={}", id, e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/{pid}/movies/{mid}")
    public ResponseEntity<?> addMovie(
            @PathVariable("pid") Long pid,
            @PathVariable("mid") Long mid,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            playlistService.addMovieToPlaylist(pid, mid, principal.getName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка API при добавлении фильма id={} в плейлист id={}", mid, pid, e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/{pid}/movies/{mid}")
    public ResponseEntity<?> removeMovie(
            @PathVariable("pid") Long pid,
            @PathVariable("mid") Long mid,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            playlistService.removeMovieFromPlaylist(pid, mid, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            log.error("Ошибка API при удалении фильма id={} из плейлиста id={}", mid, pid, e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
