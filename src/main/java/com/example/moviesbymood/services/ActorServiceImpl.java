package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.models.Actor;
import com.example.moviesbymood.repositories.ActorRepository;
import com.example.moviesbymood.converters.ActorConverter;
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
public class ActorServiceImpl implements ActorService {

    private static final Logger log = LoggerFactory.getLogger(ActorServiceImpl.class);
    private final ActorRepository actorRepo;
    private final ActorConverter actorConverter;

    @Override
    public List<Actor> findAll() {
        try {
            return actorRepo.findAll();
        } catch (Exception e) {
            log.error("Ошибка при получении списка актёров", e);
            throw e;
        }
    }

    @Override
    public Actor create(Actor actor) {
        try {
            return actorRepo.save(actor);
        } catch (Exception e) {
            log.error("Ошибка при создании актёра {}", actor.getActorFullName(), e);
            throw e;
        }
    }

    @Override
    public void save(Actor actor) {
        try {
            actorRepo.save(actor);
        } catch (Exception e) {
            log.error("Ошибка при сохранении актёра {}", actor.getActorFullName(), e);
            throw e;
        }
    }

    @Override
    public Actor update(Long id, Actor actor) {
        try {
            Actor existing = actorRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Актёр не найден: " + id));
            existing.setActorFullName(actor.getActorFullName());
            existing.setActorBirthDate(actor.getActorBirthDate());
            existing.setActorBiography(actor.getActorBiography());
            return actorRepo.save(existing);
        } catch (Exception e) {
            log.error("Ошибка при обновлении актёра id={}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            actorRepo.deleteMovieActorsByActorId(id);
            actorRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении актёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public Actor findById(Long id) {
        try {
            return actorRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Актёр не найден: " + id));
        } catch (Exception e) {
            log.error("Ошибка при поиске актёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public ActorDto findDtoById(Long id) {
        try {
            Actor a = findById(id);
            return actorConverter.convert(a);
        } catch (Exception e) {
            log.error("Ошибка при формировании DTO актёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<ActorDto> findAllDto() {
        try {
            return actorRepo.findAll()
                    .stream()
                    .map(actorConverter::convert)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении DTO актёров", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!actorRepo.existsById(id)) {
                throw new EntityNotFoundException("Актёр не найден: " + id);
            }
            actorRepo.deleteMovieActorsByActorId(id);
            actorRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении актёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<ActorDto> searchByName(String q) {
        try {
            return actorRepo.findByActorFullNameContainingIgnoreCase(q)
                    .stream()
                    .map(actorConverter::convert)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при поиске актёров по '{}'", q, e);
            throw e;
        }
    }
}
