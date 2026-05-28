package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.DirectorDto;
import com.example.moviesbymood.models.Director;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DirectorConverter implements Converter<Director, DirectorDto> {
    @Override
    public DirectorDto convert(Director director) {
        return DirectorDto.builder()
                .directorId(director.getDirectorId())
                .directorFullName(director.getDirectorFullName())
                .directorBirthDate(director.getDirectorBirthDate())
                .directorBiography(director.getDirectorBiography())
                .directorPhoto(director.getDirectorPhoto() != null
                        ? "/files/" + director.getDirectorPhoto().getFileInfoFilename()
                        : null)
                .build();
    }
}
