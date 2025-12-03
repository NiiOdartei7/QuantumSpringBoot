package com.example.quantumspringboot.controller;

import com.example.quantumspringboot.dto.UpdateUserDTO;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.dto.UserResponseDTO;
import com.example.quantumspringboot.entity.Clearance;
import com.example.quantumspringboot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    @GetMapping("/v1/user")
    public ResponseEntity<UserResponseDTO> findUser(@Valid
                                                           @RequestBody String email
    ){
        return ResponseEntity.ok(service.findUserByEmail(email));
    }

    @PatchMapping("/v1/user")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid
                                                    @RequestBody UpdateUserDTO userRequestDTO
                                                      ){
        return ResponseEntity.ok(service.updateUser(userRequestDTO.getEmail(), userRequestDTO.getDeptName(),
                userRequestDTO.getLevel() ));
    }

}
