package com.example.moviesbymood.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Пользователь уже сущестует: " + email);
    }
}