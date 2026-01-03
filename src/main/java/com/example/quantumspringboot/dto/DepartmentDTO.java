package com.example.quantumspringboot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class DepartmentDTO {
    private UUID id;

    @NotBlank(message ="This field is required")
    private String name;
}
