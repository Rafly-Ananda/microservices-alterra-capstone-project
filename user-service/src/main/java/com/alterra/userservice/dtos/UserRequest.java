package com.alterra.userservice.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotNull(message = "username is required.")
    private String username;
    @NotNull(message = "email is required.")
    private String email;
    @NotNull(message = "password is required.")
    private String password;
    @NotNull(message = "role is required.")
    private String role;
}
