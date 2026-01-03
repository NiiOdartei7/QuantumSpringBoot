package com.example.quantumspringboot.dto;

import com.example.quantumspringboot.entity.Role;
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
    private String role;

}
