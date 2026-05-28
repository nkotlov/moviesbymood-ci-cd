package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.models.Actor;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.services.ActorService;
import com.example.moviesbymood.services.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actors")
public class ActorController {

    private static final Logger log = LoggerFactory.getLogger(ActorController.class);

    private final ActorService actorService;
    private final FileStorageService fileStorageService;
    private final FileInfoRepository fileInfoRepository;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=actors";
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("actorDto", new ActorDto());
        return "actors/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("actorDto") ActorDto dto,
            BindingResult br,
            @RequestParam(value="photoFile", required=false) MultipartFile photoFile
    ) {
        if (br.hasErrors()) {
            return "actors/form";
        }
        try {
            Actor actor = actorService.create(dto.toEntity());
            if (photoFile != null && !photoFile.isEmpty()) {
                String stored = fileStorageService.saveFile(photoFile);
                FileInfo fi = fileInfoRepository.findByFileInfoFilename(stored)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + stored));
                actor.setActorPhoto(fi);
                actorService.save(actor);
            }
            return "redirect:/admin?tab=actors";
        } catch (Exception e) {
            log.error("Ошибка при создании актёра {}", dto, e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("actorDto", actorService.findDtoById(id));
            return "actors/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования актёра id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("actorDto") ActorDto dto,
            BindingResult br,
            @RequestParam(value="photoFile", required=false) MultipartFile photoFile
    ) {
        if (br.hasErrors()) {
            return "actors/form";
        }
        try {
            Actor actor = actorService.update(id, dto.toEntity());
            if (photoFile != null && !photoFile.isEmpty()) {
                String stored = fileStorageService.saveFile(photoFile);
                FileInfo fi = fileInfoRepository.findByFileInfoFilename(stored)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + stored));
                actor.setActorPhoto(fi);
            }
            actorService.save(actor);
            return "redirect:/admin?tab=actors";
        } catch (Exception e) {
            log.error("Ошибка при обновлении актёра id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            actorService.delete(id);
            return "redirect:/admin?tab=actors";
        } catch (Exception e) {
            log.error("Ошибка при удалении актёра id={}", id, e);
            throw e;
        }
    }
}
