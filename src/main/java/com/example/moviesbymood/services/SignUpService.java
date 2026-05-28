package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RegistrationForm;
import com.example.moviesbymood.exception.UserAlreadyExistsException;

public interface SignUpService {
    void register(RegistrationForm form) throws UserAlreadyExistsException;
}