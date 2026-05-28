package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.CreatePlaylistRequest;
import com.example.moviesbymood.dto.PlaylistDto;

import java.util.List;

public interface PlaylistService {
    PlaylistDto createPlaylist(String userEmail, CreatePlaylistRequest request);
    List<PlaylistDto> getUserPlaylists(String userEmail);
    PlaylistDto getPlaylistById(Long playlistId, String userEmail);
    void deletePlaylist(Long playlistId, String userEmail);
    PlaylistDto updatePlaylist(Long playlistId, String userEmail, CreatePlaylistRequest request);
    void addMovieToPlaylist(Long playlistId, Long movieId, String userEmail);
    void removeMovieFromPlaylist(Long playlistId, Long movieId, String userEmail);
}
