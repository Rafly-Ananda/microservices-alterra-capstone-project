package com.alterra.userservice.services;

import com.alterra.userservice.dtos.GlobalResponse;
import com.alterra.userservice.dtos.UserRequest;
import com.alterra.userservice.dtos.UserResponse;
import com.alterra.userservice.entities.UserEntity;
import com.alterra.userservice.exceptions.EmailAlreadyUsedException;
import com.alterra.userservice.exceptions.UserNotFoundException;
import com.alterra.userservice.exceptions.UsernameAlreadyUsedException;
import com.alterra.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;


    public ResponseEntity<GlobalResponse> createUser(UserRequest userRequest) {
        String encryptedPass = encryptionService.passwordEncryptor(userRequest.getPassword());
        UserEntity userEntity = UserEntity.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(encryptedPass)
                .role(userRequest.getRole())
                .build();

        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyUsedException(userRequest.getUsername());
        }

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException(userRequest.getEmail());
        }

        UserEntity response = userRepository.save(userEntity);
        UserResponse userSanitized = UserResponse.builder()
                .user_id(response.getUser_id())
                .username(response.getUsername())
                .email(response.getEmail())
                .role(response.getRole())
                .build();

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User Created.")
                .status(200)
                .data(List.of(userSanitized))
                .build(), HttpStatus.CREATED);
    }

    public ResponseEntity<GlobalResponse> getAllUser() {
        List<UserEntity> users = userRepository.findAll();

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Users Found.")
                .status(200)
                .data(users.stream().map(this::mapToUserResponse).toList())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> getSingleUser(Integer id) {
        UserResponse user = userRepository.findById(id).map(this::mapToUserResponse).orElseThrow(() -> new UserNotFoundException((id)));

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User Found.")
                .status(200)
                .data(List.of(user))
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> updateUser(UserEntity userEntity, Integer id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setUsername(userEntity.getUsername());
        user.setEmail(userEntity.getEmail());
        user.setRole(userEntity.getRole());

        if (!userEntity.getPassword().isEmpty()) {
            String encryptedPass = encryptionService.passwordEncryptor(userEntity.getPassword());
            user.setPassword(encryptedPass);
        }

        UserEntity updatedUser = userRepository.save(user);
        UserResponse userSanitized = UserResponse.builder()
                .user_id(updatedUser.getUser_id())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .build();

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User Updated.")
                .status(200)
                .data(List.of(userSanitized))
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> deleteUser(Integer id) {
        UserResponse user = userRepository.findById(id).map(this::mapToUserResponse).orElseThrow(() -> new UserNotFoundException((id)));
        userRepository.deleteById(id);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("User With Id " + id + " Is Deleted.")
                .status(200)
                .data(List.of(user))
                .build(), HttpStatus.OK);
    }

    private UserResponse mapToUserResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .user_id(userEntity.getUser_id())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .build();
    }

}


