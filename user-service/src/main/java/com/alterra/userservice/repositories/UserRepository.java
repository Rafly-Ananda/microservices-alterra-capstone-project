package com.alterra.userservice.repositories;

import com.alterra.userservice.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
//    @Query(value = "SELECT username, email, role FROM users WHERE users.id = ?1")
//    Optional<UserEntity> findById(Integer id);
//
//    @Query( value = "SELECT * FROM users")
//    List<UserEntity> findAllNative();
}
