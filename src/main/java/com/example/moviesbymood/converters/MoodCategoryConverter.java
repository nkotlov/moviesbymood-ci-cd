package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.MoodCategoryDto;
import com.example.moviesbymood.models.MoodCategory;
import com.example.moviesbymood.models.Movie;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MoodCategoryConverter implements Converter<MoodCategory, MoodCategoryDto> {

    @Override
    public MoodCategoryDto convert(MoodCategory mood) {
        MoodCategoryDto dto = MoodCategoryDto.builder()
                .moodId(mood.getMoodId())
                .moodName(mood.getMoodName())
                .moodDescription(mood.getMoodDescription())
                .movieIds(
                        mood.getMoodMovies().stream()
                                .map(Movie::getMovieId)
                                .collect(Collectors.toSet())
                )
                .build();

        if (mood.getMoodIcon() != null) {
            dto.setIconFilename(mood.getMoodIcon().getFileInfoFilename());
        }
        return dto;
    }
}
