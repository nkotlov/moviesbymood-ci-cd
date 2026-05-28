package com.example.moviesbymood.controllers;

import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.UserRepository;
import com.example.moviesbymood.security.details.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${google.oauth.auth-url}")
    private String authUrl;

    @Value("${google.oauth.token-url}")
    private String tokenUrl;

    @Value("${google.oauth.scope}")
    private String scope;

    @GetMapping("/oauth2/authorize/google")
    public String redirectToGoogle() {
        String authorizationUri = UriComponentsBuilder
                .fromHttpUrl(authUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
        return "redirect:" + authorizationUri;
    }

    @GetMapping("/oauth2/callback/google")
    public String handleGoogleCallback(
            String code,
            String error,
            Model model,
            HttpServletRequest request
    ) {
        if (error != null) {
            model.addAttribute("error", "Ошибка от Google: " + error);
            return "login";
        }
        if (code == null || code.isEmpty()) {
            model.addAttribute("error", "Не получен код авторизации от Google");
            return "login";
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        if (!tokenResponse.getStatusCode().is2xxSuccessful() ||
                tokenResponse.getBody() == null
        ) {
            model.addAttribute("error", "Не удалось получить токен от Google");
            return "login";
        }

        String idToken = (String) tokenResponse.getBody().get("id_token");
        if (idToken == null) {
            model.addAttribute("error", "Google не вернул id_token");
            return "login";
        }

        String[] jwtParts = idToken.split("\\.");
        if (jwtParts.length < 2) {
            model.addAttribute("error", "Некорректный id_token");
            return "login";
        }

        String payloadJson;
        try {
            payloadJson = new String(Base64.getUrlDecoder().decode(jwtParts[1]));
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка декодирования id_token");
            return "login";
        }

        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при разборе JSON из id_token");
            return "login";
        }

        String email = (String) payload.get("email");
        Boolean emailVerified = (Boolean) payload.getOrDefault("email_verified", false);
        String name = (String) payload.getOrDefault("name", "");
        if (email == null || !emailVerified) {
            model.addAttribute("error", "Google не подтвердил email пользователя");
            return "login";
        }

        User user = userRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .userEmail(email)
                            .userPassword(UUID.randomUUID().toString())
                            .userNickname(!name.isBlank() ? name : email.split("@")[0])
                            .userRole("ROLE_USER")
                            .userRegistrationDate(LocalDate.now())
                            .confirmed(true)
                            .oauthOnly(true)
                            .build();
                    return userRepository.save(newUser);
                });

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                sc
        );
        return "redirect:/movies";
    }
}
