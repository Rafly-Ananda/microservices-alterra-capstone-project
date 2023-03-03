package com.alterra.userservice.controllers;

import com.alterra.userservice.entities.UserEntity;
import com.alterra.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity createUser(@RequestBody UserEntity userEntity) {
        return userService.createUser(userEntity);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserEntity> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<UserEntity> getSingleUser(@PathVariable Integer id) {
        return userService.getSingleUser(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity updateUser(@RequestBody UserEntity userEntity, @PathVariable Integer id) {
        return userService.updateUser(userEntity, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
        return "User deleted.";
    }
}
