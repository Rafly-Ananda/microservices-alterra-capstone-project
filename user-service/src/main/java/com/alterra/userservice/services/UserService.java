package com.alterra.userservice.services;

import com.alterra.userservice.entities.UserEntity;
import com.alterra.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public UserEntity createUser(UserEntity userEntity) {
        String encryptedPass = encryptionService.passwordEncryptor(userEntity.getPassword());
        userEntity.setPassword(encryptedPass);
        return userRepository.save(userEntity);
    }

    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> getSingleUser(Integer id) {
        return userRepository.findById(id);
    }

    public UserEntity updateUser(UserEntity userEntity, Integer id) {
        UserEntity selectedUser = userRepository.findById(id).orElseThrow();
        selectedUser.setUsername(userEntity.getUsername());
        selectedUser.setEmail(userEntity.getEmail());
        selectedUser.setPassword(userEntity.getPassword());
        selectedUser.setRole(userEntity.getRole());
        return userRepository.save(selectedUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

}


