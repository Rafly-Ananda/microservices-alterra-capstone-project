package com.alterra.userservice.controllers;

import com.alterra.userservice.dtos.*;
import com.alterra.userservice.entities.UserEntity;
import com.alterra.userservice.services.EncryptionService;
import com.alterra.userservice.services.RabbitMQProducerService;
import com.alterra.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@EnableRabbit
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final RabbitMQProducerService rabbitMQProducerService;

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck() {
        return "Working, Ready.";
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);

        EmailRequest msg = new EmailRequest();
        msg.setTo(createdUser.getEmail());
        msg.setSubject("AlterraCommerce Registration ✨");
        msg.setBodyText("Hey, thank you for registering. Amazing deals, and interesting products are coming your way!");
        rabbitMQProducerService.sendJsonMessage(msg);


        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User Created.")
                .status(200)
                .data(List.of(createdUser))
                .build(), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse>  getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("{id}")
    public ResponseEntity<GlobalResponse> getSingleUser(@PathVariable Integer id) {
        return userService.getSingleUser(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateUser(@RequestBody UserEntity userEntity, @PathVariable Integer id) {
        return userService.updateUser(userEntity, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<GlobalResponse> deleteUser(@PathVariable Integer id){
        return userService.deleteUser(id);
    }

    @PostMapping("/verify")
    public ResponseEntity<GlobalResponse> verifyUser(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        return userService.verifyCredential(loginUserRequest);
    }
    @GetMapping("/find")
    public ResponseEntity<GlobalResponse> findByUsername(@RequestParam("username") String username) {
        return userService.findByUsername(username);
    }

}
