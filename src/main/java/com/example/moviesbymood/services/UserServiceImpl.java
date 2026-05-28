package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.UserDto;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserDto findByUsername(String email) {
        try {
            User u = userRepository.findByUserEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
            return toDto(u);
        } catch (Exception e) {
            log.error("Ошибка при загрузке пользователя '{}'", email, e);
            throw e;
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        try {
            return userRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя id={}", id, e);
            throw e;
        }
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .userId(u.getUserId())
                .userEmail(u.getUserEmail())
                .userNickname(u.getUserNickname())
                .confirmed(u.isConfirmed())
                .userRole(u.getUserRole())
                .userRegistrationDate(u.getUserRegistrationDate())
                .oauthOnly(u.isOauthOnly())
                .avatarFilename(u.getAvatar() != null ? u.getAvatar().getFileInfoFilename() : null)
                .build();
    }
}
