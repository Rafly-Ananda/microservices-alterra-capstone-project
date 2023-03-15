package com.beyonder.authservice.exception;

import com.beyonder.authservice.dto.GlobalResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<GlobalResponse> handleLoginFailedException(
            LoginFailedException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<GlobalResponse> handleRegistrationFailedException(
            RegistrationFailedException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<GlobalResponse> handleInvalidPasswordException(
            InvalidPasswordException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalResponse> handleUserNotFoundException(
            InvalidPasswordException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.NOT_FOUND);
    }




}
