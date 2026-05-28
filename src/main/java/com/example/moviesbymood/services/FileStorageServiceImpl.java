package com.example.moviesbymood.services;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.repositories.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileInfoRepository fileInfoRepository;

    @Value("${storage.path}")
    private String storagePath;

    @Override public String getStoragePath() { return storagePath; }

    @Override
    @Transactional
    public String registerFile(String filename, String contentType, Path fullPath) {
        FileInfo info = FileInfo.builder()
                .fileInfoFilename(filename)
                .fileInfoUrl(fullPath.toString())
                .fileInfoType(contentType)
                .fileInfoUploadedAt(Instant.now())
                .build();
        fileInfoRepository.save(info);
        return filename;
    }

    @Override
    @Transactional
    public String saveFile(MultipartFile uploadFile) {
        String storedName = UUID.randomUUID() + "_" + uploadFile.getOriginalFilename();
        Path target = Path.of(storagePath, storedName);

        try (InputStream in = uploadFile.getInputStream()) {
            Files.createDirectories(target.getParent());
            Files.copy(in, target);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить файл на диск", e);
        }

        FileInfo info = FileInfo.builder()
                .fileInfoFilename(storedName)
                .fileInfoUrl(target.toString())
                .fileInfoType(uploadFile.getContentType())
                .fileInfoUploadedAt(Instant.now())
                .build();
        fileInfoRepository.save(info);

        return storedName;
    }

    @Override
    public void writeFileToResponse(String filename, HttpServletResponse response) {
        FileInfo info = fileInfoRepository.findByFileInfoFilename(filename)
                .orElseThrow(() -> new IllegalArgumentException("Файл не найден: " + filename));

        response.setContentType(info.getFileInfoType());
        try (InputStream in = Files.newInputStream(Path.of(info.getFileInfoUrl()))) {
            IOUtils.copy(in, response.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось отдать файл", e);
        }
    }
}
