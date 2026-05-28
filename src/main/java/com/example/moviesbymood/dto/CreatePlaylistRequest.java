package com.example.moviesbymood.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlaylistRequest {
    @NotBlank(message="Название не может быть пустым")
    private String playlistName;
    private String posterFilename;
    private Set<Long> movieIds = new HashSet<>();
}
