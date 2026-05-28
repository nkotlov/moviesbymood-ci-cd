package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto findByUsername(String email);
    List<UserDto> findAllUsers();
    void deleteById(Long id);
}
