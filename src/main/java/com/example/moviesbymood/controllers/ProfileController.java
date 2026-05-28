package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.UserDto;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.repositories.UserRepository;
import com.example.moviesbymood.services.FavoriteMovieService;
import com.example.moviesbymood.services.FavoriteMoodService;
import com.example.moviesbymood.services.FileStorageService;
import com.example.moviesbymood.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final UserService userService;
    private final UserRepository userRepo;
    private final FileStorageService fileStorageService;
    private final FileInfoRepository fileInfoRepo;
    private final FavoriteMovieService favoriteMovieService;
    private final FavoriteMoodService favoriteMoodService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        try {
            String email = principal.getName();
            UserDto userDto = userService.findByUsername(email);
            model.addAttribute("user", userDto);

            List<?> favMovies = favoriteMovieService.getFavoriteMovies();
            Set<Long> favMovieIds = favoriteMovieService.getFavoriteMovieIds();
            model.addAttribute("favoriteMovies", favMovies);
            model.addAttribute("favoriteMovieIds", favMovieIds);

            List<?> favMoods = favoriteMoodService.getFavoriteMoods()
                    .stream().map(com.example.moviesbymood.dto.MoodCategoryDto::fromEntity)
                    .collect(Collectors.toList());
            Set<Long> favMoodIds = favoriteMoodService.getFavoriteMoodIds();
            model.addAttribute("favoriteMoods", favMoods);
            model.addAttribute("favoriteMoodIds", favMoodIds);

            model.addAttribute("activeTab", "profile");
            return "profile";
        } catch (Exception e) {
            log.error("Ошибка при загрузке профиля", e);
            throw e;
        }
    }

    @PostMapping("/profile/avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile avatar,
                               Principal principal) {
        try {
            String email = principal.getName();
            if (!avatar.isEmpty()) {
                String stored = fileStorageService.saveFile(avatar);
                FileInfo fi = fileInfoRepo.findByFileInfoFilename(stored)
                        .orElseThrow();
                User u = userRepo.findByUserEmail(email).orElseThrow();
                u.setAvatar(fi);
                userRepo.save(u);
            }
            return "redirect:/profile";
        } catch (Exception e) {
            log.error("Ошибка при обновлении аватара", e);
            throw e;
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "change_password";
    }

    @PostMapping("/profile/change-password")
    public String processChangePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model) {
        try {
            String email = principal.getName();
            User user = userRepo.findByUserEmail(email).orElseThrow();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (!encoder.matches(currentPassword, user.getUserPassword())) {
                model.addAttribute("error", "Текущий пароль неверен");
                return "change_password";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                return "change_password";
            }

            user.setUserPassword(encoder.encode(newPassword));
            user.setOauthOnly(false);
            userRepo.save(user);
            return "redirect:/profile?passwordChanged";
        } catch (Exception e) {
            log.error("Ошибка при смене пароля", e);
            throw e;
        }
    }

    @GetMapping("/profile/set-password")
    public String showSetPasswordForm() {
        return "set_password";
    }

    @PostMapping("/profile/set-password")
    public String processSetPassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model) {
        try {
            String email = principal.getName();
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                return "set_password";
            }
            User user = userRepo.findByUserEmail(email).orElseThrow();
            user.setUserPassword(new BCryptPasswordEncoder().encode(newPassword));
            user.setOauthOnly(false);
            userRepo.save(user);
            return "redirect:/profile?passwordSet";
        } catch (Exception e) {
            log.error("Ошибка при установке пароля", e);
            throw e;
        }
    }

    @PostMapping("/profile/favorite/movie/{id}")
    @ResponseBody
    public void toggleProfileMovie(@PathVariable Long id, Principal principal) {
        try {
            if (principal == null) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
            }
            String email = principal.getName();
            log.info("Toggle favorite movie id={} for '{}'", id, email);
            favoriteMovieService.toggleFavoriteMovie(id);
        } catch (Exception e) {
            log.error("Ошибка при переключении favorite movie", e);
            throw e;
        }
    }

    @PostMapping("/profile/favorite/mood/{id}")
    @ResponseBody
    public void toggleProfileMood(@PathVariable Long id, Principal principal) {
        try {
            if (principal == null) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);
            }
            String email = principal.getName();
            favoriteMoodService.toggleFavoriteMood(id);
        } catch (Exception e) {
            log.error("Ошибка при переключении favorite mood", e);
            throw e;
        }
    }
}
