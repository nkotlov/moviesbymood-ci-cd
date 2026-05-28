package com.example.moviesbymood.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Controller
public class SimpleAjaxController {

    private static final Logger log = LoggerFactory.getLogger(SimpleAjaxController.class);

    @GetMapping("/ajax")
    public String ajaxPage() {
        return "ajax";
    }

    @PostMapping(path = "/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> handleAjax(@RequestBody Map<String, String> payload) {
        try {
            String text = payload.getOrDefault("text", "ни о чём");
            return Map.of("message", "Сервер принял: " + text);
        } catch (Exception e) {
            log.error("Ошибка при обработке AJAX", e);
            return Collections.singletonMap("error", "Внутренняя ошибка");
        }
    }
}
