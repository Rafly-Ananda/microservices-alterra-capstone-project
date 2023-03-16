package com.beyonder.authservice.service.impl;

import com.beyonder.authservice.dto.CreateUserRequestDTO;
import com.beyonder.authservice.dto.GlobalResponse;
import com.beyonder.authservice.dto.LoginUserRequestDTO;
import com.beyonder.authservice.exception.InvalidPasswordException;
import com.beyonder.authservice.service.TokenService;
import com.beyonder.authservice.util.PasswordValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {
    final static String UserServiceUrl = "http://user-service:8082/api/v1/user";
    private final RestTemplate restTemplate;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    @Override
    public String generatedToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(username)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    @Override
    public ResponseEntity<GlobalResponse> detailUserByToken(String token) {
        // Bearer xxxxjwtcodexxxx
        String newToken = token.split(" ")[1];
        Jwt jwtToken = jwtDecoder.decode(newToken);
        String data = jwtToken.getSubject();
        String userFindByUsernameUrl = UserServiceUrl + "/find?username=" + data;
        try {
            ResponseEntity<GlobalResponse> userDetailResponse = restTemplate.getForEntity(userFindByUsernameUrl, GlobalResponse.class);

            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message(userDetailResponse.getBody().getMessage())
                    .status(userDetailResponse.getBody().getStatus())
                    .data(userDetailResponse.getBody().getData())
                    .build(), HttpStatus.CREATED);
        } catch (HttpClientErrorException e) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(e.getResponseBodyAsString());
                String errorMessage = rootNode.get("message").asText();
                return new ResponseEntity<>(GlobalResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(errorMessage)
                        .status(e.getRawStatusCode())
                        .data(new ArrayList<>())
                        .build(), HttpStatus.OK);
            } catch (Exception ex) {
                throw new RuntimeException("Error parsing response body");
            }
        } catch (Exception e) {
            log.error("Unexpected error while get detail user (" + userFindByUsernameUrl + ") :", e);
            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("Unexpected error while get detail user")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .data(new ArrayList<>())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional()
    public ResponseEntity<GlobalResponse> registerUser(CreateUserRequestDTO userRequest) {
        if (!PasswordValidator.isValid(userRequest.getPassword())) {
            throw new InvalidPasswordException("Password Invalid : password must be Alphanumeric");
        }
        try {
            ResponseEntity<GlobalResponse> response = null;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateUserRequestDTO> requestEntity = new HttpEntity<>(userRequest, headers);
            response = restTemplate.exchange(UserServiceUrl, HttpMethod.POST, requestEntity, GlobalResponse.class);
            log.info("Registration successful");
            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message(response.getBody().getMessage())
                    .status(response.getBody().getStatus())
                    .data(new ArrayList<>())
                    .build(), HttpStatus.CREATED);

        } catch (HttpClientErrorException ex) {
            log.info("HttpException while Rest Template to user-service " + ex);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(ex.getResponseBodyAsString());
                String message = jsonNode.get("message").asText();

                return new ResponseEntity<>(GlobalResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(message)
                        .status(ex.getStatusCode().value())
                        .data(new ArrayList<>())
                        .build(), ex.getStatusCode());
            } catch (Exception e) {
                log.info(String.valueOf(ex));
                return new ResponseEntity<>(GlobalResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("Internal Server Error : Unexpected error parsing JSON")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .data(new ArrayList<>())
                        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


    }

    @Override
    public ResponseEntity<GlobalResponse> loginUser(LoginUserRequestDTO loginUserRequestDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginUserRequestDTO> requestEntity = new HttpEntity<>(loginUserRequestDTO, headers);
            ResponseEntity<GlobalResponse> response = restTemplate.exchange(UserServiceUrl + "/verify", HttpMethod.POST, requestEntity, GlobalResponse.class);

            String token = generatedToken(loginUserRequestDTO.getUsername());

            log.info("Authentication successful" + response);
            Jwt jwtToken = jwtDecoder.decode(token);
            String data = jwtToken.getSubject();
            log.info("login as " + data);

            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("token created")
                    .status(response.getStatusCodeValue())
                    .data(Collections.singletonList(token))
                    .build(), response.getStatusCode());

        } catch (HttpClientErrorException ex) {
            log.info("HttpException while Rest Template to user-service " + ex);
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(ex.getResponseBodyAsString());
                String message = jsonNode.get("message").asText();

                return new ResponseEntity<>(GlobalResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(message)
                        .status(ex.getStatusCode().value())
                        .data(new ArrayList<>())
                        .build(), ex.getStatusCode());
            } catch (Exception e) {
                log.info(String.valueOf(ex));
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    return new ResponseEntity<>(GlobalResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .message("Username or Password incorrect")
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(new ArrayList<>())
                            .build(), HttpStatus.UNAUTHORIZED);
                } else {
                    return new ResponseEntity<>(GlobalResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .message("Internal Server Error : Unexpected error parsing JSON")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .data(new ArrayList<>())
                            .build(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

    }
}
