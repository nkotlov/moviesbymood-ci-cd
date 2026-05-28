package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.OmdbResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class OmdbService {

    private static final Logger log = LoggerFactory.getLogger(OmdbService.class);

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Value("${omdb.api.url}")
    private String baseUrl;

    @Value("${omdb.api.key}")
    private String apiKey;

    public OmdbResponse fetchByTitle(String title) {
        try {
            String q = URLEncoder.encode(title, java.nio.charset.StandardCharsets.UTF_8);
            URI uri = URI.create(baseUrl + "?apikey=" + apiKey + "&t=" + q);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(resp.body(), OmdbResponse.class);

        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при запросе OMDb для '{}'", title, e);
            throw new RuntimeException("OMDb fetch error: " + e.getMessage(), e);
        }
    }
}
