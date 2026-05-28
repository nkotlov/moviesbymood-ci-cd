package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.MoodCategory;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodCategoryDto {
    private Long moodId;
    private String moodName;
    private String moodDescription;
    private String iconFilename;
    @Builder.Default
    private Set<Long> movieIds = new HashSet<>();
    public static MoodCategoryDto fromEntity(MoodCategory entity) {
        if (entity == null) return null;
        MoodCategoryDto dto = MoodCategoryDto.builder()
                .moodId(entity.getMoodId())
                .moodName(entity.getMoodName())
                .moodDescription(entity.getMoodDescription())
                .movieIds(entity.getMoodMovies().stream()
                        .map(m -> m.getMovieId())
                        .collect(Collectors.toSet()))
                .build();
        FileInfo icon = entity.getMoodIcon();
        if (icon != null) {
            dto.setIconFilename(icon.getFileInfoFilename());
        }
        return dto;
    }

    public MoodCategory toEntity() {
        MoodCategory entity = new MoodCategory();
        entity.setMoodName(this.moodName);
        entity.setMoodDescription(this.moodDescription);
        return entity;
    }
}
