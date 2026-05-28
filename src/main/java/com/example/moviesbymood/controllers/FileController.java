package com.example.moviesbymood.controllers;

import com.example.moviesbymood.models.FileInfo;
import com.example.moviesbymood.repositories.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileInfoRepository fileInfoRepository;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serve(@PathVariable String filename) {
        try {
            FileInfo info = fileInfoRepository.findByFileInfoFilename(filename)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            Path path = Path.of(info.getFileInfoUrl());
            Resource res = new UrlResource(path.toUri());
            if (!res.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            String cd = "inline; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(info.getFileInfoType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, cd)
                    .body(res);

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Ошибка при отдаче файла '{}'", filename, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
