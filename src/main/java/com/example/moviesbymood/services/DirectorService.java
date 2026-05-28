package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.models.Director;

import java.util.List;

public interface DirectorService {
    List<Director> findAll();
    Director create(Director director);
    Director update(Long id, Director director);
    Director save(Director director);
    void deleteById(Long id);
    Director findById(Long id);
    DirectorDto findDtoById(Long id);
    void delete(Long id);
    List<DirectorDto> findAllDto();
    List<DirectorDto> searchByName(String namePart);
}