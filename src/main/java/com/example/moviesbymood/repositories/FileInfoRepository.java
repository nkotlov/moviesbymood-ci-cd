package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findByFileInfoFilename(String filename);
}
