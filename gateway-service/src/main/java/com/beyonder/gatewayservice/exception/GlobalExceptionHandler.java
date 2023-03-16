package com.beyonder.gatewayservice.exception;

import com.beyonder.gatewayservice.dto.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<GlobalResponse> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse> handleException(Exception ex, WebRequest request) {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(null)
                .build();
        return new ResponseEntity<>(globalResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
