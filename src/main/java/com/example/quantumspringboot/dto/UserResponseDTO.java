package com.example.quantumspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;

    private String email;

    private String userAccessPolicy;

//    public UserResponseDTO(UUID id, String email, String userAccessPolicy) {
//        this.id = id;
//        this.email = email;
//        this.userAccessPolicy = userAccessPolicy;
//
//    }

}
