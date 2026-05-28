package com.example.moviesbymood.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationForm {
    @NotEmpty @Email
    private String email;

    @NotEmpty @Size(min = 8)
    private String password;

    @NotEmpty @Size(min = 3)
    private String nickname;
}
