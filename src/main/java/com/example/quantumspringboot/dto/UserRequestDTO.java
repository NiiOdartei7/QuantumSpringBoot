package com.example.quantumspringboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @Pattern(regexp = "^(.+)@(.+)$")
    @NotBlank(message = "field is required")
    private String email;

    @NotBlank(message ="This field is required")
    private String password;

    private String deptName;

    private String level;

    public UserRequestDTO(String email, @NonNull String password){
        this.email = email;
        this.password = password;

    }

    public UserRequestDTO(String email, @NonNull String deptName, String level){
        this.email = email;
        this.deptName = deptName;
        this.level = level;

    }

}
