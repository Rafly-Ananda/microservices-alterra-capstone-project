package com.alterra.userservice.exceptions;

import com.alterra.userservice.dtos.GlobalResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(404)
                .data(null)
                .build();

        return new ResponseEntity<>(globalResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    public ResponseEntity<GlobalResponse> handleUsernameAlreadyUsedException(
            UsernameAlreadyUsedException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(400)
                .data(null)
                .build();

        return new ResponseEntity<>(globalResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<GlobalResponse> handleEmailAlreadyUsedException(
            EmailAlreadyUsedException ex, WebRequest request) {

        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .status(400)
                .data(null)
                .build();

        return new ResponseEntity<>(globalResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();


        GlobalResponse globalResponse = GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Insufficient Argument.")
                .status(status.value())
                .data(Collections.singletonList(errors))
                .build();




        return new ResponseEntity<>(globalResponse, HttpStatus.BAD_REQUEST);
    }

}
