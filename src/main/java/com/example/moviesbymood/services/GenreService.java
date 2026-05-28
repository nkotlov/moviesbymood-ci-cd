package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.GenreDto;
import com.example.moviesbymood.models.Genre;
import java.util.List;

public interface GenreService {
    List<Genre> findAll();
    Genre create(Genre genre);
    Genre update(Long id, Genre genre);
    void deleteById(Long id);
    Genre findById(Long id);
    List<GenreDto> findAllDto();
    List<GenreDto> searchByName(String substring);
}