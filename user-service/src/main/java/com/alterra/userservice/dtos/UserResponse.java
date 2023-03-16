package com.alterra.userservice.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer user_id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
