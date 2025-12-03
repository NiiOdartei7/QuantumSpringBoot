package com.example.quantumspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String email;
    private String deptName;
    private String level;


//    public UpdateUserDTO(String email, String deptName, String level){
//
//    }
}
