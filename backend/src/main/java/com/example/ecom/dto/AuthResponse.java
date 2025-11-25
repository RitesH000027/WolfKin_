package com.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;
    private Long expiresIn;
    
    public static AuthResponse of(String token, UserDto user, Long expiresIn) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .user(user)
                .expiresIn(expiresIn)
                .build();
    }
}