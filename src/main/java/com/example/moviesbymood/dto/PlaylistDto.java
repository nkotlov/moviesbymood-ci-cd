// src/main/java/com/example/moviesbymood/dto/PlaylistDto.java
package com.example.moviesbymood.dto;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Playlist;
import lombok.*;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistDto {
    private Long playlistId;
    private String playlistName;
    private Instant playlistCreatedAt;
    private Long userId;
    private String posterFilename;
    private String posterUrl;

    private Set<Long> movieIds;
    private Set<MovieDto> playlistMovies;

    public static PlaylistDto fromEntity(Playlist pl) {
        String filename = null, url = null;
        FileInfo fi = pl.getPlaylistPoster();
        if (fi != null) {
            filename = fi.getFileInfoFilename();
            url = "/files/" + filename;
        }
        Set<Long> ids = pl.getPlaylistMovies().stream()
                .map(Movie::getMovieId)
                .collect(Collectors.toSet());
        Set<MovieDto> movies = pl.getPlaylistMovies().stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toSet());
        return PlaylistDto.builder()
                .playlistId(pl.getPlaylistId())
                .playlistName(pl.getPlaylistName())
                .playlistCreatedAt(pl.getPlaylistCreatedAt())
                .userId(pl.getPlaylistUser().getUserId())
                .posterFilename(filename)
                .posterUrl(url)
                .movieIds(ids)
                .playlistMovies(movies)
                .build();
    }
}
