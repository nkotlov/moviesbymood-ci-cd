package com.example.moviesbymood.controllers;

import com.example.moviesbymood.dto.RegistrationForm;
import com.example.moviesbymood.exception.UserAlreadyExistsException;
import com.example.moviesbymood.services.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final SignUpService signUpService;

    @GetMapping("/signUp")
    public String signupForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "signUp";
    }

    @PostMapping("/signUp")
    public String doSignup(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult br,
                           Model model) {
        if (br.hasErrors()) {
            return "signUp";
        }
        try {
            signUpService.register(form);
            return "redirect:/login?registered";
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("error", ex.getMessage());
            return "signUp";
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя email={}", form.getEmail(), e);
            throw e;
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "registered", required = false) String reg,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный email или пароль");
        }
        if (reg != null) {
            model.addAttribute("msg", "Регистрация прошла успешно");
        }
        return "login";
    }
}
