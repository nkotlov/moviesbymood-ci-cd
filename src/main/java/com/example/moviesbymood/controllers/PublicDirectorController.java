package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.services.DirectorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/directors")
@RequiredArgsConstructor
public class PublicDirectorController {
    private static final Logger log = LoggerFactory.getLogger(PublicDirectorController.class);
    private final DirectorService directorService;

    @GetMapping("/{id:\\d+}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            DirectorDto director = directorService.findDtoById(id);
            model.addAttribute("director", director);
            return "directors/detail";
        } catch (Exception e) {
            log.error("Ошибка при получении режиссёра id=" + id, e);
            throw e;
        }
    }
}
