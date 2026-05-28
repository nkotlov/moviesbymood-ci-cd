package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.MovieDto;
import com.example.moviesbymood.dto.PlaylistDto;
import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.models.Movie;
import com.example.moviesbymood.models.Playlist;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.FileInfoRepository;
import com.example.moviesbymood.repositories.MovieRepository;
import com.example.moviesbymood.repositories.PlaylistRepository;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final FileInfoRepository fileInfoRepository;

    private User findUser(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + email));
    }

    private PlaylistDto toDto(Playlist pl) {
        Set<Long> movieIds = pl.getPlaylistMovies().stream()
                .map(Movie::getMovieId)
                .collect(Collectors.toSet());

        Set<MovieDto> movieDtos = pl.getPlaylistMovies().stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toSet());

        String filename = null, url = null;
        FileInfo fi = pl.getPlaylistPoster();
        if (fi != null && fi.getFileInfoFilename() != null) {
            filename = fi.getFileInfoFilename();
            url      = "/files/" + filename;
        }

        return PlaylistDto.builder()
                .playlistId(pl.getPlaylistId())
                .playlistName(pl.getPlaylistName())
                .playlistCreatedAt(pl.getPlaylistCreatedAt())
                .userId(pl.getPlaylistUser().getUserId())
                .posterFilename(filename)
                .posterUrl(url)
                .movieIds(movieIds)
                .playlistMovies(movieDtos)
                .build();
    }


    @Override
    public PlaylistDto createPlaylist(String userEmail, CreatePlaylistRequest request) {
        User user = findUser(userEmail);

        Playlist pl = new Playlist();
        pl.setPlaylistName(request.getPlaylistName());
        pl.setPlaylistUser(user);

        if (request.getPosterFilename() != null) {
            FileInfo poster = fileInfoRepository
                    .findByFileInfoFilename(request.getPosterFilename())
                    .orElseThrow(() -> new IllegalStateException("FileInfo не найден: "
                            + request.getPosterFilename()));
            pl.setPlaylistPoster(poster);
        }

        if (request.getMovieIds() != null && !request.getMovieIds().isEmpty()) {
            Set<Movie> movies = request.getMovieIds().stream()
                    .map(id -> movieRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + id)))
                    .collect(Collectors.toSet());
            pl.getPlaylistMovies().addAll(movies);
        }

        Playlist saved = playlistRepository.save(pl);
        return toDto(saved);
    }

    @Override
    public PlaylistDto updatePlaylist(Long playlistId, String userEmail, CreatePlaylistRequest req) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().equals(user)) {
            throw new AccessDeniedException("Нельзя редактировать чужой плейлист");
        }
        pl.setPlaylistName(req.getPlaylistName());
        if (req.getPosterFilename() != null) {
            FileInfo fi = fileInfoRepository.findByFileInfoFilename(req.getPosterFilename())
                    .orElseThrow(() -> new IllegalStateException("FileInfo не найден: " + req.getPosterFilename()));
            pl.setPlaylistPoster(fi);
        }
        pl.getPlaylistMovies().clear();
        if (req.getMovieIds() != null) {
            req.getMovieIds().forEach(mid -> {
                Movie m = movieRepository.findById(mid)
                        .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + mid));
                pl.getPlaylistMovies().add(m);
            });
        }
        playlistRepository.save(pl);
        return toDto(pl);
    }

    @Override
    public java.util.List<PlaylistDto> getUserPlaylists(String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        return playlistRepository.findByPlaylistUserOrderByPlaylistCreatedAtDesc(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDto getPlaylistById(Long playlistId, String userEmail) {
        User user = findUser(userEmail);
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        return toDto(pl);
    }
    @Override
    public void deletePlaylist(Long playlistId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        playlistRepository.delete(pl);
    }

    @Override
    public void addMovieToPlaylist(Long playlistId, Long movieId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + movieId));
        pl.getPlaylistMovies().add(mv);
        playlistRepository.save(pl);
    }

    @Override
    public void removeMovieFromPlaylist(Long playlistId, Long movieId, String userEmail) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Нет пользователя: " + userEmail));
        Playlist pl = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Плейлист не найден: " + playlistId));
        if (!pl.getPlaylistUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Доступ запрещён к этому плейлисту");
        }
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден: " + movieId));
        pl.getPlaylistMovies().remove(mv);
        playlistRepository.save(pl);
    }
}
