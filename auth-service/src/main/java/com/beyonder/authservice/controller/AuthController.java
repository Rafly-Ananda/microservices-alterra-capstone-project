package com.beyonder.authservice.controller;

import com.beyonder.authservice.dto.*;
import com.beyonder.authservice.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<GlobalResponse> registerUser(@RequestBody CreateUserRequestDTO userRequest) {
        return tokenService.registerUser(userRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse> loginUser(@RequestBody LoginUserRequestDTO loginUserRequestDTO) {
        return tokenService.loginUser(loginUserRequestDTO);
    }

    @GetMapping("/detail-user")
    public ResponseEntity<GlobalResponse> detailUserByToken(@RequestHeader(name = "Authorization") String tokenBearer) {
        return tokenService.detailUserByToken(tokenBearer);
    }
}
