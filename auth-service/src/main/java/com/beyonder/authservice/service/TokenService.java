package com.beyonder.authservice.service;

import com.beyonder.authservice.dto.CreateUserRequestDTO;
import com.beyonder.authservice.dto.GlobalResponse;
import com.beyonder.authservice.dto.LoginUserRequestDTO;
import com.beyonder.authservice.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface TokenService {

    String generatedToken(String username);


    ResponseEntity<GlobalResponse> detailUserByToken(String token);

    @Transactional()
    ResponseEntity<GlobalResponse> registerUser(CreateUserRequestDTO userDTO);

    ResponseEntity<GlobalResponse> loginUser(LoginUserRequestDTO loginUserRequestDTO);
}
