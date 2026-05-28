package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.RegistrationForm;
import com.example.moviesbymood.exception.UserAlreadyExistsException;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SignUpServiceImpl implements SignUpService {

    private static final Logger log = LoggerFactory.getLogger(SignUpServiceImpl.class);

    private final UserRepository   userRepo;
    private final PasswordEncoder  passwordEncoder;

    @Override
    public void register(RegistrationForm form) throws UserAlreadyExistsException {
        try {
            if (userRepo.findByUserEmail(form.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("Email уже занят");
            }
            if (userRepo.findByUserNickname(form.getNickname()).isPresent()) {
                throw new UserAlreadyExistsException("Nickname уже занят");
            }
            User user = User.builder()
                    .userEmail(form.getEmail())
                    .userPassword(passwordEncoder.encode(form.getPassword()))
                    .userNickname(form.getNickname())
                    .userRole("ROLE_USER")
                    .userRegistrationDate(LocalDate.now())
                    .confirmed(true)
                    .build();
            userRepo.save(user);
        } catch (UserAlreadyExistsException e) {
            log.warn("Регистрация не выполнена: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя email={}", form.getEmail(), e);
            throw e;
        }
    }
}
