package com.beyonder.authservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class UserDTO {
    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private String role;
}