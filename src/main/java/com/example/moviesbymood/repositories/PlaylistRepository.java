package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.Playlist;
import com.example.moviesbymood.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByPlaylistUserOrderByPlaylistCreatedAtDesc(User user);
}
