package com.example.quantumspringboot.controller;

import com.example.quantumspringboot.dto.AuthenticationResponse;
import com.example.quantumspringboot.dto.DepartmentDTO;
import com.example.quantumspringboot.dto.DepartmentRequestDTO;
import com.example.quantumspringboot.dto.UserRequestDTO;
import com.example.quantumspringboot.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepartmentController {
    private final DepartmentService service;


    @PostMapping("/v1/department")
    public ResponseEntity<DepartmentDTO> addDepartment(@Valid
                                                           @RequestBody DepartmentRequestDTO requestDTO
    ){
        return ResponseEntity.ok(service.createDepartment(requestDTO.getName()));
    }
}
