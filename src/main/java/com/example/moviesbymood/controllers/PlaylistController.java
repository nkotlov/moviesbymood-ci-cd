package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.services.FileStorageService;
import com.example.moviesbymood.services.MovieService;
import com.example.moviesbymood.services.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final MovieService movieService;
    private final FileStorageService fileStorageService;

    @GetMapping("/new")
    public String newPlaylistForm(Model model) {
        model.addAttribute("createRequest", new CreatePlaylistRequest());
        model.addAttribute("allMovies", movieService.findAll());
        return "playlists/create";
    }

    @PostMapping
    public String createPlaylist(@ModelAttribute("createRequest") @Valid CreatePlaylistRequest req,
                                 BindingResult br,
                                 @RequestParam(value="posterFile", required=false) MultipartFile posterFile,
                                 Authentication auth,
                                 Model model) {
        if (br.hasErrors()) {
            model.addAttribute("allMovies", movieService.findAll());
            return "playlists/create";
        }
        if (posterFile != null && !posterFile.isEmpty()) {
            String fn = fileStorageService.saveFile(posterFile);
            req.setPosterFilename(fn);
        }
        String userEmail = auth.getName();
        PlaylistDto created = playlistService.createPlaylist(userEmail, req);
        return "redirect:/playlists/" + created.getPlaylistId();
    }

    @GetMapping("/{id}/edit")
    public String editPlaylistForm(@PathVariable Long id,
                                   Authentication auth,
                                   Model model) {
        String email = auth.getName();
        PlaylistDto dto = playlistService.getPlaylistById(id, email);
        CreatePlaylistRequest req = new CreatePlaylistRequest();
        req.setPlaylistName(dto.getPlaylistName());
        req.setMovieIds(dto.getMovieIds());
        req.setPosterFilename(dto.getPosterFilename());

        model.addAttribute("playlist", dto);
        model.addAttribute("updateRequest", req);
        model.addAttribute("allMovies", movieService.findAll());
        return "playlists/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePlaylist(@PathVariable Long id,
                                 @ModelAttribute("updateRequest") @Valid CreatePlaylistRequest req,
                                 BindingResult br,
                                 @RequestParam(value="posterFile", required=false) MultipartFile posterFile,
                                 Authentication auth,
                                 Model model) {
        if (br.hasErrors()) {
            model.addAttribute("allMovies", movieService.findAll());
            return "playlists/edit";
        }
        if (posterFile != null && !posterFile.isEmpty()) {
            req.setPosterFilename(fileStorageService.saveFile(posterFile));
        }
        playlistService.updatePlaylist(id, auth.getName(), req);
        return "redirect:/playlists/" + id;
    }

    @GetMapping
    public String listPlaylists(Model model, Authentication auth) {
        String email = auth.getName();
        List<PlaylistDto> playlists = playlistService.getUserPlaylists(email);
        model.addAttribute("userPlaylists", playlists);
        model.addAttribute("activeTab", "playlists");
        return "playlists/list";
    }

    @GetMapping("/{id}")
    public String viewPlaylist(@PathVariable Long id,
                               Authentication auth,
                               Model model) {
        model.addAttribute("playlist",
                playlistService.getPlaylistById(id, auth.getName()));
        return "playlists/details";
    }

    @PostMapping("/{id}/delete")
    public String deletePlaylist(@PathVariable Long id, Authentication auth) {
        playlistService.deletePlaylist(id, auth.getName());
        return "redirect:/playlists";
    }
}
