package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.models.Director;
import com.example.moviesbymood.repositories.DirectorRepository;
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
public class DirectorServiceImpl implements DirectorService {

    private static final Logger log = LoggerFactory.getLogger(DirectorServiceImpl.class);
    private final DirectorRepository directorRepo;

    @Override
    public List<Director> findAll() {
        try {
            return directorRepo.findAll();
        } catch (Exception e) {
            log.error("Ошибка при получении всех режиссёров", e);
            throw e;
        }
    }

    @Override
    public Director create(Director director) {
        try {
            return directorRepo.save(director);
        } catch (Exception e) {
            log.error("Ошибка при сохранении нового режиссёра: {}", director.getDirectorFullName(), e);
            throw e;
        }
    }

    @Override
    public Director update(Long id, Director director) {
        try {
            Director existing = directorRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Режиссёр не найден: " + id));
            existing.setDirectorFullName(director.getDirectorFullName());
            existing.setDirectorBirthDate(director.getDirectorBirthDate());
            existing.setDirectorBiography(director.getDirectorBiography());
            return directorRepo.save(existing);
        } catch (Exception e) {
            log.error("Ошибка при обновлении режиссёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public Director save(Director director) {
        try {
            return directorRepo.save(director);
        } catch (Exception e) {
            log.error("Ошибка при сохранении режиссёра: {}", director.getDirectorFullName(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        directorRepo.deleteMovieDirectorsByDirectorId(id);

        directorRepo.deleteById(id);
    }

    @Override
    public DirectorDto findDtoById(Long id) {
        Director d = directorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Режиссёр не найден: " + id));
        return DirectorDto.builder()
                .directorId(d.getDirectorId())
                .directorFullName(d.getDirectorFullName())
                .directorBirthDate(d.getDirectorBirthDate())
                .directorBiography(d.getDirectorBiography())
                .directorPhoto(d.getDirectorPhoto() != null
                        ? "/files/" + d.getDirectorPhoto().getFileInfoFilename()
                        : null)
                .build();
    }

    @Override
    public List<DirectorDto> findAllDto() {
        try {
            return directorRepo.findAll()
                    .stream()
                    .map(DirectorDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении всех DTO режиссёров", e);
            throw e;
        }
    }

    @Override
    public Director findById(Long id) {
        try {
            return directorRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Режиссёр не найден: " + id));
        } catch (Exception e) {
            log.error("Ошибка при поиске режиссёра id={}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (!directorRepo.existsById(id)) {
                throw new EntityNotFoundException("Режиссёр не найден: " + id);
            }
            directorRepo.deleteMovieDirectorsByDirectorId(id);
            directorRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении режиссёра id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<DirectorDto> searchByName(String namePart) {
        try {
            return directorRepo
                    .findByDirectorFullNameContainingIgnoreCase(namePart)
                    .stream()
                    .map(DirectorDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при поиске режиссёров по имени='{}'", namePart, e);
            throw e;
        }
    }
}
