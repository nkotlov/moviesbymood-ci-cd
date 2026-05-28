package com.example.moviesbymood.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String userEmail;
    private String userNickname;
    private boolean confirmed;
    private String userRole;
    private LocalDate userRegistrationDate;
    private boolean oauthOnly;
    private String avatarFilename;
}
