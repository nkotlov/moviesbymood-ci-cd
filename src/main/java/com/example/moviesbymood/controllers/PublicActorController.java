package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.services.ActorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actors")
@RequiredArgsConstructor
public class PublicActorController {
    private static final Logger log = LoggerFactory.getLogger(PublicActorController.class);
    private final ActorService actorService;

    @GetMapping("/{id:\\d+}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            ActorDto actor = actorService.findDtoById(id);
            model.addAttribute("actor", actor);
            return "actors/detail";
        } catch (Exception e) {
            log.error("Ошибка при получении актёра id=" + id, e);
            throw e;
        }
    }
}
