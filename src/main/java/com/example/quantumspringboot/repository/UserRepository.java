package com.example.quantumspringboot.repository;


import com.example.quantumspringboot.entity.Department;
import com.example.quantumspringboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    User findUserById(UUID id);

    boolean existsUserById(UUID id);

}