package com.example.moviesbymood.services;

import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.nio.file.Path;

public interface FileStorageService {
    String saveFile(MultipartFile uploadFile);
    void writeFileToResponse(String filename, HttpServletResponse response);
    String registerFile(String filename, String contentType, Path fullPath);
    String getStoragePath();
}
