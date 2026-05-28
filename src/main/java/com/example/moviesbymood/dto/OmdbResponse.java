package com.example.moviesbymood.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbResponse {
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Poster")
    private String poster;
    @JsonProperty("Runtime")
    private String runtime;

    @JsonProperty("Response")
    private String response;
    @JsonProperty("Error")
    private String error;
}