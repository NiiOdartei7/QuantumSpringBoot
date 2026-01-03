package com.example.quantumspringboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @Pattern(regexp = "^(.+)@(.+)$")
    @NotBlank(message ="This field is required")
    private String email;
    @NotBlank(message ="This field is required")
    private String password;
}
