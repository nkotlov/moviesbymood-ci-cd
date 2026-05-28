package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.models.Actor;

import java.util.List;

public interface ActorService {
    List<Actor> findAll();
    Actor create(Actor actor);
    Actor update(Long id, Actor actor);
    void deleteById(Long id);
    Actor findById(Long id);
    ActorDto findDtoById(Long id);
    void delete(Long id);
    List<ActorDto> findAllDto();
    List<ActorDto> searchByName(String namePart);
    void save(Actor actor);
}
