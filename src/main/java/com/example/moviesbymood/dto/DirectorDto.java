package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.Director;
import com.example.moviesbymood.models.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDto {
    private Long   directorId;
    private String directorFullName;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate directorBirthDate;
    private String directorBiography;
    private String directorPhoto;

    public static DirectorDto fromEntity(Director director) {
        if (director == null) return null;
        String url = null;
        FileInfo fi = director.getDirectorPhoto();
        if (fi != null) {
            url = fi.getFileInfoUrl();
        }
        return DirectorDto.builder()
                .directorId(director.getDirectorId())
                .directorFullName(director.getDirectorFullName())
                .directorBirthDate(director.getDirectorBirthDate())
                .directorBiography(director.getDirectorBiography())
                .directorPhoto(url)
                .build();
    }

    public Director toEntity() {
        Director d = new Director();
        d.setDirectorId(this.directorId);
        d.setDirectorFullName(this.directorFullName);
        d.setDirectorBirthDate(this.directorBirthDate);
        d.setDirectorBiography(this.directorBiography);
        return d;
    }
}
