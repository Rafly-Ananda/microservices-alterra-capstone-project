package com.alterra.userservice.controllers;

import com.alterra.userservice.dtos.GlobalResponse;
import com.alterra.userservice.dtos.UserRequest;
import com.alterra.userservice.dtos.UserResponse;
import com.alterra.userservice.entities.UserEntity;
import com.alterra.userservice.services.EncryptionService;
import com.alterra.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final EncryptionService encryptionService;

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck() {
        return "Working, Ready.";
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
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

}
