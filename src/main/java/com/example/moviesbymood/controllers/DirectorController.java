package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.models.Director;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.services.DirectorService;
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
@RequestMapping("/admin/directors")
public class DirectorController {

    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);
    private final DirectorService directorService;
    private final FileStorageService fileStorageService;
    private final FileInfoRepository fileInfoRepository;

    @GetMapping
    public String list() {
        return "redirect:/admin?tab=directors";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("directorDto", new DirectorDto());
        return "directors/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("directorDto") DirectorDto dto,
            BindingResult br,
            @RequestParam(value="photoFile", required=false) MultipartFile photoFile
    ) {
        if (br.hasErrors()) {
            return "directors/form";
        }
        try {
            Director dir = directorService.create(dto.toEntity());
            if (photoFile != null && !photoFile.isEmpty()) {
                String stored = fileStorageService.saveFile(photoFile);
                FileInfo fi = fileInfoRepository.findByFileInfoFilename(stored)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + stored));
                dir.setDirectorPhoto(fi);
                directorService.save(dir);
            }
            return "redirect:/admin?tab=directors";
        } catch (Exception e) {
            log.error("Ошибка при создании режиссёра: dto={} ", dto, e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("directorDto", directorService.findDtoById(id));
            return "directors/form";
        } catch (Exception e) {
            log.error("Ошибка при загрузке формы редактирования режиссёра id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("directorDto") DirectorDto dto,
            BindingResult br,
            @RequestParam(value="photoFile", required=false) MultipartFile photoFile
    ) {
        if (br.hasErrors()) {
            return "directors/form";
        }
        try {
            Director dir = directorService.update(id, dto.toEntity());
            if (photoFile != null && !photoFile.isEmpty()) {
                String stored = fileStorageService.saveFile(photoFile);
                FileInfo fi = fileInfoRepository.findByFileInfoFilename(stored)
                        .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + stored));
                dir.setDirectorPhoto(fi);
                directorService.save(dir);
            }
            return "redirect:/admin?tab=directors";
        } catch (Exception e) {
            log.error("Ошибка при обновлении режиссёра id={}: dto={}", id, dto, e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            directorService.delete(id);
            return "redirect:/admin?tab=directors";
        } catch (Exception e) {
            log.error("Ошибка при удалении режиссёра id={}", id, e);
            throw e;
        }
    }
}
