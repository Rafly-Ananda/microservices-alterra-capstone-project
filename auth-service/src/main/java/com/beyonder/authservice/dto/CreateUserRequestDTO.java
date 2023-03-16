package com.beyonder.authservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}
