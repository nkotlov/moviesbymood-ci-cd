package com.example.moviesbymood.services;

import com.example.moviesbymood.converters.GenreConverter;
import com.example.moviesbymood.dto.GenreDto;
import com.example.moviesbymood.models.Genre;
import com.example.moviesbymood.repositories.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private static final Logger log = LoggerFactory.getLogger(GenreServiceImpl.class);
    private final GenreRepository genreRepo;
    private final GenreConverter genreConverter;

    @Override
    public List<Genre> findAll() {
        try {
            return genreRepo.findAll();
        } catch (Exception e) {
            log.error("Ошибка получения списка жанров", e);
            throw e;
        }
    }

    @Override
    public Genre create(Genre genre) {
        try {
            return genreRepo.save(genre);
        } catch (Exception e) {
            log.error("Ошибка создания жанра '{}'", genre.getGenreName(), e);
            throw e;
        }
    }

    @Override
    public Genre update(Long id, Genre genre) {
        try {
            Genre existing = genreRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Жанр не найден: " + id));
            existing.setGenreName(genre.getGenreName());
            return genreRepo.save(existing);
        } catch (Exception e) {
            log.error("Ошибка обновления жанра id={}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            genreRepo.deleteMovieGenresByGenreId(id);
            genreRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка удаления жанра id={}", id, e);
            throw e;
        }
    }

    @Override
    public Genre findById(Long id) {
        try {
            return genreRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Жанр не найден: " + id));
        } catch (Exception e) {
            log.error("Ошибка поиска жанра id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<GenreDto> findAllDto() {
        try {
            return genreRepo.findAll().stream()
                    .map(GenreDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка получения DTO жанров", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> searchByName(String q) {
        try {
            return genreRepo.findByGenreNameContainingIgnoreCase(q).stream()
                    .map(genreConverter::convert)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка поиска жанров по '{}'", q, e);
            throw e;
        }
    }
}
